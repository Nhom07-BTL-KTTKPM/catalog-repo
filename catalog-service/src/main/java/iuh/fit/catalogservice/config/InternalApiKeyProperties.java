package iuh.fit.catalogservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "internal.api")
public class InternalApiKeyProperties {

    private String headerName;
    private String key;
}
