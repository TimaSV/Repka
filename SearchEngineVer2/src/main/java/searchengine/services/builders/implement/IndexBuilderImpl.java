package searchengine.services.builders.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.services.builders.interfaces.IndexBuilder;
import searchengine.dto.IndexDto;
import searchengine.lemmatizator.Lemmatizator;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.utils.HtmlCleaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class IndexBuilderImpl implements IndexBuilder {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final Lemmatizator lemmatizator;
    private List<IndexDto> indexDtoList;

    @Override
    public void run(Site site) {
        Iterable<Page> pageList = pageRepository.findBySiteId(site);
        List<Lemma> lemmaList = lemmaRepository.findBySiteId(site);
        indexDtoList = new ArrayList<>();
        log.info("Строим индексы для сайта: " + site.getUrl());
        for (Page page : pageList) {
            if (page.getCode() < 400) {
                long pageId = page.getId();
                String content = page.getContent();
                String title = HtmlCleaner.clear(content, "title");
                String body = HtmlCleaner.clear(content, "body");
                HashMap<String, Integer> titleList = lemmatizator.getLemmaList(title);
                HashMap<String, Integer> bodyList = lemmatizator.getLemmaList(body);

                for (Lemma lemma : lemmaList) {
                    Long lemmaId = lemma.getId();
                    String keyWord = lemma.getLemma();
                    if (titleList.containsKey(keyWord) || bodyList.containsKey(keyWord)) {
                        float totalRank = 0.0F;
                        if (titleList.get(keyWord) != null) {
                            Float titleRank = Float.valueOf(titleList.get(keyWord));
                            totalRank += titleRank;
                        }
                        if (bodyList.get(keyWord) != null) {
                            float bodyRank = (float) (bodyList.get(keyWord) * 0.8);
                            totalRank += bodyRank;
                        }
                        indexDtoList.add(new IndexDto(pageId, lemmaId, totalRank));
                    }
                }
            } else {
                log.debug("Bad status code - " + page.getCode());
            }
        }
    }

    @Override
    public List<IndexDto> getIndexList() {
        return indexDtoList;
    }
}
