package searchengine.services.builders.interfaces;

import searchengine.dto.IndexDto;
import searchengine.model.Site;

import java.util.List;

public interface IndexBuilder {
    void run(Site site);

    List<IndexDto> getIndexList();
}
