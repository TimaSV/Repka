package main.apiResponses;

import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatisticsInitializer {
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;

    public static SiteRepository siteRepo;
    public static PageRepository pageRepo;
    public static LemmaRepository lemmaRepo;


    public void init() {
        siteRepo = siteRepository;
        pageRepo = pageRepository;
        lemmaRepo = lemmaRepository;
    }
}
