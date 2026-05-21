package iuh.fit.catalogservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingProperties {

    private boolean enabled = true;
    private int vectorDimension = 768;
    private double minScore = 0.35;

    private Ollama ollama = new Ollama();

    private Reindex reindex = new Reindex();

    @Getter
    @Setter
    public static class Reindex {
        private boolean enabled = true;
        private String cron = "0 0 * * * *";
        private int batchSize = 200;
        private Lock lock = new Lock();
    }

    @Getter
    @Setter
    public static class Lock {
        private boolean enabled = true;
        private long key = 731925;
    }

    @Getter
    @Setter
    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "nomic-embed-text";
        private Duration timeout = Duration.ofSeconds(20);
    }
}
