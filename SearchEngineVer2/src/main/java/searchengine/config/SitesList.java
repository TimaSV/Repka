package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import searchengine.dto.SiteDto;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "config")
public class SitesList {
    private List<SiteDto> sites;
}
