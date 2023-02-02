package searchengine.services.builders.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.services.builders.interfaces.IndexBuilder;
import searchengine.services.builders.interfaces.LemmaBuilder;
import searchengine.config.SitesList;
import searchengine.dto.IndexDto;
import searchengine.dto.LemmaDto;
import searchengine.dto.PageDto;
import searchengine.dto.SiteDto;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.enums.Status;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
@Slf4j
public class SiteBuilder implements Runnable {

    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmaBuilder lemmaBuilder;
    private final IndexBuilder indexBuilder;
    private final String url;
    private final SitesList sitesList;


    @Override
    public void run() {
        long begin = System.currentTimeMillis();
        if (siteRepository.findByUrl(url) != null) {
            deleteDataFromSite();
        }
        log.info("Сайт \"" + getName() + "\" индексируется");
        saveDateSite();

        try {

            List<PageDto> pageDtoList = getPageDtoList();
            saveToBase(pageDtoList);
            getLemmasPage();
            indexingWords();
        } catch (InterruptedException e) {
            log.error("Индексация сайта - " + url + " прервана");
            errorSite();
        }
        log.info("Сайт \"" + url + "\" построен за " + (System.currentTimeMillis() - begin) / 1000 + " сек");
    }

    private List<PageDto> getPageDtoList() throws InterruptedException {
        if (!Thread.interrupted()) {
            String urlFormat = url + "/";
            List<PageDto> pageDtoVector = new Vector<>();
            List<String> urlList = new Vector<>();
            ForkJoinPool forkJoinPool = new ForkJoinPool(processorCoreCount);
            List<PageDto> pages = forkJoinPool.invoke(new PageBuilder(urlFormat, pageDtoVector, urlList));
            return new CopyOnWriteArrayList<>(pages);
        } else throw new InterruptedException();
    }

    private void saveToBase(List<PageDto> pages) throws InterruptedException {
        if (!Thread.interrupted()) {
            List<Page> pageList = new CopyOnWriteArrayList<>();
            Site site = siteRepository.findByUrl(url);

            for (PageDto page : pages) {
                int start = page.getUrl().indexOf(url) + url.length();
                String pageFormat = page.getUrl().substring(start);
                pageList.add(new Page(site, pageFormat, page.getCode(),
                        page.getContent()));
            }
            pageRepository.flush();
            pageRepository.saveAll(pageList);
        } else {
            throw new InterruptedException();
        }
    }

    private void getLemmasPage() {
        if (!Thread.interrupted()) {
            Site site = siteRepository.findByUrl(url);
            site.setStatusTime(new Date());
            lemmaBuilder.run(site);
            List<LemmaDto> lemmaDtoList = lemmaBuilder.getLemmaDtoList();
            List<Lemma> lemmaList = new CopyOnWriteArrayList<>();

            for (LemmaDto lemmaDto : lemmaDtoList) {
                lemmaList.add(new Lemma(lemmaDto.getLemma(), lemmaDto.getFrequency(), site));
            }
            lemmaRepository.flush();
            lemmaRepository.saveAll(lemmaList);
        } else {
            throw new RuntimeException();
        }
    }

    private void indexingWords() throws InterruptedException {
        if (!Thread.interrupted()) {
            Site site = siteRepository.findByUrl(url);
            indexBuilder.run(site);
            List<IndexDto> indexDtoList = new CopyOnWriteArrayList<>(indexBuilder.getIndexList());
            List<Index> indexList = new CopyOnWriteArrayList<>();
            site.setStatusTime(new Date());
            for (IndexDto indexDto : indexDtoList) {
                Page page = pageRepository.getReferenceById(indexDto.getPageID());
                Lemma lemma = lemmaRepository.getReferenceById(indexDto.getLemmaID());
                indexList.add(new Index(page, lemma, indexDto.getRank()));
            }
            indexRepository.flush();
            indexRepository.saveAll(indexList);
            site.setStatusTime(new Date());
            site.setStatus(Status.INDEXED);
            siteRepository.save(site);

        } else {
            throw new InterruptedException();
        }
    }

    private void deleteDataFromSite() {
        Site site = siteRepository.findByUrl(url);
        site.setStatus(Status.INDEXING);
        site.setName(getName());
        site.setStatusTime(new Date());
        siteRepository.save(site);
        siteRepository.flush();
        siteRepository.delete(site);
    }

    private void saveDateSite() {
        Site site = new Site();
        site.setUrl(url);
        site.setName(getName());
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepository.flush();
        siteRepository.save(site);
    }

    private void errorSite() {
        Site site = new Site();
        site.setLastError("Индексация остановлена");
        site.setStatus(Status.FAILED);
        site.setStatusTime(new Date());
        siteRepository.save(site);
    }

    private String getName() {
        List<SiteDto> siteDtos = sitesList.getSites();
        for (SiteDto map : siteDtos) {
            if (map.getUrl().equals(url)) {
                return map.getName();
            }
        }
        return "";
    }
}

