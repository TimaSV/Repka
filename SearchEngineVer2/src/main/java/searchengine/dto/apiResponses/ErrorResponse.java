package searchengine.dto.apiResponses;

import lombok.Value;

@Value
public class ErrorResponse {
    boolean result;

    String error;
}
