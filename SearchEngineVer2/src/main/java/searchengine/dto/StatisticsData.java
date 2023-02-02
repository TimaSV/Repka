package searchengine.dto;

import lombok.Value;

import java.util.List;

@Value
public class StatisticsData {
    private TotalStatistics total;
    private List<DetailedStatisticsDto> detailed;
}
