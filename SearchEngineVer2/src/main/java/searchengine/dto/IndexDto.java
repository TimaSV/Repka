package searchengine.dto;


import lombok.Value;

@Value
public class IndexDto {
    Long pageID;
    Long lemmaID;
    Float rank;
}
