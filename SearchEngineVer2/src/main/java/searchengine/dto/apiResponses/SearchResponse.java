package searchengine.dto.apiResponses;

import lombok.Getter;
import lombok.Setter;
import searchengine.dto.SearchDto;

import java.util.List;

@Setter
@Getter
public class SearchResponse {
    private boolean result;
    private int count;
    private List<SearchDto> data;

   public SearchResponse(boolean result){
        this.result = result;
    }

    public SearchResponse(boolean result, int count, List<SearchDto> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }
}
