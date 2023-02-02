package searchengine.services.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.SiteDto;
import searchengine.model.Site;
import searchengine.model.enums.Status;
import searchengine.services.builders.interfaces.IndexBuilder;
import searchengine.services.builders.interfaces.LemmaBuilder;
import searchengine.services.builders.implement.SiteBuilder;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.interfaces.IndexingService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmaBuilder lemmaBuilder;
    private final IndexBuilder indexBuilder;
    private final SitesList sitesList;

    @Override
    public boolean urlIndexing(String url) {
        if (urlCheck(url)) {
            log.info("Сайт: " + url + " переиндексируется");
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            executorService.submit(new SiteBuilder(pageRepository, siteRepository, lemmaRepository, indexRepository, lemmaBuilder, indexBuilder, url, sitesList));
            executorService.shutdown();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean indexingAll() {
        if (isIndexingActive()) {
            log.debug("Индексация уже запущена");
            return false;
        } else {
            List<SiteDto> siteDtoList = sitesList.getSites();
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            for (SiteDto siteDto : siteDtoList) {
                String url = siteDto.getUrl();
                Site siteEntity = new Site();
                siteEntity.setName(siteDto.getName());
                executorService.submit(new SiteBuilder(pageRepository, siteRepository, lemmaRepository, indexRepository, lemmaBuilder, indexBuilder, url, sitesList));
            }
            executorService.shutdown();
        }
        return true;
    }

    @Override
    public boolean stopIndexing() {
        if (isIndexingActive()) {
            log.info("Останавливаем индексацию");
            executorService.shutdownNow();
            return true;
        } else {
            log.info("Индексация не может быть остановлена т.к. не была запущена");
            return false;
        }
    }

    private boolean isIndexingActive() {
        siteRepository.flush();
        Iterable<Site> siteList = siteRepository.findAll();
        for (Site site : siteList) {
            if (site.getStatus() == Status.INDEXING) {
                return true;
            }
        }
        return false;
    }

    private boolean urlCheck(String url) {
        List<SiteDto> urlList = sitesList.getSites();
        for (SiteDto siteDto : urlList) {
            if (siteDto.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }
}
