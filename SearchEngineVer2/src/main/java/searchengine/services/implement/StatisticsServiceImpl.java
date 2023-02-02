package searchengine.services.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.DetailedStatisticsDto;
import searchengine.dto.StatisticsData;
import searchengine.dto.StatisticsResponse;
import searchengine.dto.TotalStatistics;

import searchengine.model.Site;
import searchengine.model.enums.Status;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.interfaces.StatisticsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;

    private TotalStatistics getTotal() {
        Long sites = siteRepository.count();
        Long pages = pageRepository.count();
        Long lemmas = lemmaRepository.count();
        return new TotalStatistics(sites, pages, lemmas, true);
    }

    private DetailedStatisticsDto getDetailed(Site site) {
        String url = site.getUrl();
        String name = site.getName();
        Status status = site.getStatus();
        Date statusTime = site.getStatusTime();
        String error = site.getLastError();
        long pages = pageRepository.countBySiteId(site);
        long lemmas = lemmaRepository.countBySiteId(site);
        return new DetailedStatisticsDto(url, name, status, statusTime, error, pages, lemmas);
    }

    private List<DetailedStatisticsDto> getDetailedList() {
        List<Site> siteList = siteRepository.findAll();
        List<DetailedStatisticsDto> result = new ArrayList<>();
        for (Site site : siteList) {
            DetailedStatisticsDto item = getDetailed(site);
            result.add(item);
        }
        return result;
    }


    @Override
    public StatisticsResponse getStatistics() {
        TotalStatistics total = getTotal();
        List<DetailedStatisticsDto> list = getDetailedList();
        return new StatisticsResponse(true, new StatisticsData(total, list));
    }
}
