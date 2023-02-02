package searchengine.lemmatizator;

import java.util.HashMap;
import java.util.List;

public interface Lemmatizator {
    HashMap<String, Integer> getLemmaList(String content);
    List<String> getLemma(String word);
    List<Integer> findLemmaIndexInText(String content, String lemma);
}
