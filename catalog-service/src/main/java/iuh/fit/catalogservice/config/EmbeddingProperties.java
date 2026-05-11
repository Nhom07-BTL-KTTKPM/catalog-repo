package iuh.fit.catalogservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingProperties {

    private boolean enabled = true;
    private String provider = "gemini";
    private String apiKey;
    private String model;
    private String endpoint = "https://generativelanguage.googleapis.com/v1beta/models";
    private int vectorDimension = 768;

    private Reindex reindex = new Reindex();

    @Getter
    @Setter
    public static class Reindex {
        private boolean enabled = true;
        private String cron = "0 0 * * * *";
        private int batchSize = 200;
    }
}
