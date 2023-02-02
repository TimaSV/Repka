package searchengine.dto;

import lombok.Value;
import searchengine.model.enums.Status;

import java.util.Date;

@Value
public class DetailedStatisticsDto {
    String url;
    String name;
    Status status;
    Date statusTime;
    String error;
    long pages;
    long lemmas;
}
