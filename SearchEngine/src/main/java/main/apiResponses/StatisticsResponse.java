package main.apiResponses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.builders.SiteBuilder;
import main.model.Site;
import main.repository.impl.RepositoryMapper;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class StatisticsResponse extends Response {
    private Statistics statistics = new Statistics();

    @Data
    static class Statistics {
        private TotalStatistics total;
        private List<DetailedStatistics> detailed;

        public Statistics() {
            total = new TotalStatistics();
            detailed = new ArrayList<>();

            List<Site> sites = RepositoryMapper.siteRepo.findAll().stream()
                    .filter(site -> site.getType().equals(Site.INDEXED) ||
                            site.getType().equals(Site.FAILED) ||
                            site.getType().equals(Site.INDEXING))
                    .toList();
            for (Site site : sites) {
                DetailedStatistics detailedStatistics = new DetailedStatistics(site);
                detailed.add(detailedStatistics);
            }
        }
    }

    @Data
    static class TotalStatistics {
        private int sites;
        private int pages;
        private int lemmas;
        private boolean isIndexing;

        public TotalStatistics() {
            int siteCount = RepositoryMapper.siteRepo.countByType(Site.INDEXED) +
                    RepositoryMapper.siteRepo.countByType(Site.FAILED);
            setSites(siteCount);

            List<Site> indexedSites = RepositoryMapper.siteRepo.findAllByType(Site.INDEXED);
            setPages(RepositoryMapper.pageRepo.countBySites(indexedSites));
            setLemmas(RepositoryMapper.lemmaRepo.countBySites(indexedSites));

            setIndexing(!SiteBuilder.getIndexingSites().isEmpty());
        }
    }

    @Data
    static class DetailedStatistics {
        private String url;
        private String name;
        private String status;
        private long statusTime;
        private String error;
        private int pages;
        private int lemmas;

        public DetailedStatistics(Site site) {
            url = site.getUrl();
            name = site.getName();
            status = site.getType();
            statusTime = (site.getStatusTime().toEpochSecond(ZoneOffset.UTC) - 3 * 3600) * 1000;
            error = site.getLastError();
            pages = RepositoryMapper.pageRepo.countBySite(site);
            lemmas = RepositoryMapper.lemmaRepo.countBySite(site);
        }
    }
}
