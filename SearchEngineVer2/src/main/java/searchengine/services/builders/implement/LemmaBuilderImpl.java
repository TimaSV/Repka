package searchengine.services.builders.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.services.builders.interfaces.LemmaBuilder;
import searchengine.dto.LemmaDto;
import searchengine.lemmatizator.Lemmatizator;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.utils.HtmlCleaner;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class LemmaBuilderImpl implements LemmaBuilder {
    private final PageRepository pageRepository;
    private final Lemmatizator lemmatizator;
    private List<LemmaDto> lemmaDtoList;

    @Override
    public void run(Site site) {
        log.info("Строим леммы для сайта: " + site.getUrl());
        lemmaDtoList = new CopyOnWriteArrayList<>();
        Iterable<Page> pageList = pageRepository.findAll();
        TreeMap<String, Integer> lemmaList = new TreeMap<>();
        for (Page page : pageList) {
            String content = page.getContent();
            String title = HtmlCleaner.clear(content, "title");
            String body = HtmlCleaner.clear(content, "body");
            HashMap<String, Integer> titleList = lemmatizator.getLemmaList(title);
            HashMap<String, Integer> bodyList = lemmatizator.getLemmaList(body);
            Set<String> allWords = new HashSet<>();
            allWords.addAll(titleList.keySet());
            allWords.addAll(bodyList.keySet());
            for (String word : allWords) {
                int frequency = lemmaList.getOrDefault(word, 0) + 1;
                lemmaList.put(word, frequency);
            }
        }
        for (String lemma : lemmaList.keySet()) {
            Integer frequency = lemmaList.get(lemma);
            lemmaDtoList.add(new LemmaDto(lemma, frequency));
        }
    }

    @Override
    public List<LemmaDto> getLemmaDtoList() {
        return lemmaDtoList;
    }
}
