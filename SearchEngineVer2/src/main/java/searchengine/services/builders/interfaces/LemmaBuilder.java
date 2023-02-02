package searchengine.services.builders.interfaces;

import searchengine.dto.LemmaDto;
import searchengine.model.Site;

import java.util.List;

public interface LemmaBuilder {
    void run(Site site);

    List<LemmaDto> getLemmaDtoList();
}
