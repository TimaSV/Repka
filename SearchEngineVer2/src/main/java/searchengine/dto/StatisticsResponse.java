package searchengine.dto;

import lombok.Value;

@Value
public class StatisticsResponse {
    private boolean result;
    private StatisticsData statistics;
}
