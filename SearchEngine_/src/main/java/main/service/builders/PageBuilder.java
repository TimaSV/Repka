package main.service.builders;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.config.Props;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.repository.IndexRepository;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.repository.SiteRepository;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@NoArgsConstructor
public class PageBuilder implements Runnable {
    public static final String OK = "OK";
    public static final String NOT_FOUND = "\"Данная страница находится за пределами сайтов, " +
            "указанных в конфигурационном файле";
    public static final String SITE_NOT_INDEXED = "Нельзя индексировать страницу " +
            "сайта, если сайт ещё не индексирован";
    public static final String RUNNING = "Индексация уже запущена";

    private Site site;
    private List<Page> oldPages;
    private Page page = null;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private IndexRepository indexRepository;

    public static SiteRepository siteRepo;
    public static PageRepository pageRepo;
    public static LemmaRepository lemmaRepo;

    public static IndexRepository indexRepo;


    @PostConstruct
    public void init() {
        siteRepo = siteRepository;
        pageRepo = pageRepository;
        lemmaRepo = lemmaRepository;
        indexRepo = indexRepository;
    }

    public PageBuilder(Site site, String pagePath) {
        this.site = site;
        oldPages = pageRepo.findAllBySiteAndPathAndCode(site, pagePath, Node.OK);

        Node node = new Node(site, pagePath);
        node.setFromPageBuilder(true);
        Document doc = node.processAndReturnPageDoc();
        if (doc == null) {
            return;
        }
        int id = node.getAddedPageId();
        page = pageRepo.findById(id).orElse(null);
        if (page == null) {
            doc = null;
            return;
        }
        page.setContent(doc.outerHtml());
        page.setPath(pagePath);
    }

    @Override
    public void run() {
        log.info("Проиндексирована страница " + site.getUrl() + page.getPath());
        List<Lemma> lemmaList = lemmaRepo.findAllBySite(site);
        Map<String, Lemma> lemmas = new HashMap<>();
        for (Lemma lemma : lemmaList) {
            lemmas.put(lemma.getLemma(), lemma);
        }

        List<Index> indexList = indexRepo.findAllBySite(site);
        Map<Integer, Index> indices = new HashMap<>();
        for (Index index : indexList) {
            indices.put(index.hashCode(), index);
        }

        IndexBuilder indexBuilder = new IndexBuilder(site, page, lemmas, indices);
        indexBuilder.fillLemmasAndIndices();

        List<Lemma> lemmasToDelete = new ArrayList<>();
        if (oldPages != null && oldPages.size() > 0) {
            List<Integer> oldPageIds = oldPages.stream().map(p -> p.getId()).toList();
            for (Index index : indices.values().stream()
                    .filter(index -> oldPageIds.contains(index.getPage().getId()))
                    .toList()) {
                Lemma lemma = index.getLemma();
                lemma.setFrequency(lemma.getFrequency() - 1);
                if (lemma.getFrequency() == 0) {
                    lemmas.remove(lemma.getLemma());
                    lemmasToDelete.add(lemma);
                }
            }
        }

        lemmaRepo.deleteAllInBatch(lemmasToDelete);

        List<Index> pageIndices = new ArrayList<>();
        pageIndices.addAll(indices.values().stream()
                .filter(index -> index.getPage().getId() == page.getId())
                .toList());

        synchronized (Page.class) {
            pageRepo.saveAndFlush(page);
        }
        lemmaRepo.deleteAllInBatch(lemmasToDelete);
        lemmaRepo.saveAllAndFlush(lemmaList);
        indexRepo.saveAllAndFlush(pageIndices);
        synchronized (Page.class) {
            if (oldPages != null) {
                for (Page p : oldPages) {
                    pageRepo.deleteById(p.getId());
                }
            }
        }

        SiteBuilder.getIndexingSites().remove(site.getUrl());
    }

    public static String indexPage(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            return NOT_FOUND;
        }
        String home = url.getProtocol() + "://" + url.getHost();
        String path = url.getFile();

        if (SiteBuilder.getIndexingSites().containsKey(home)) {
            return RUNNING;
        }

        if (!Props.getAllSiteUrls().contains(home)) {
            return NOT_FOUND;
        }
        Site site = siteRepo.findByUrlAndType(home, Site.INDEXED).orElse(null);

        if (path.isEmpty()) {
            SiteBuilder.buildSingleSite(home);
        } else {
            if (site == null) {
                return SITE_NOT_INDEXED;
            }
            PageBuilder pageBuilder = new PageBuilder(site, path);
            if (pageBuilder.page == null) {
                return NOT_FOUND;
            }
            SiteBuilder.getIndexingSites().put(site.getUrl(), site);
            pageBuilder.run();
        }

        return OK;
    }
}
