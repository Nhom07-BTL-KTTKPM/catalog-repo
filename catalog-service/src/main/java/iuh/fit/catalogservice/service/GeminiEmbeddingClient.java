package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.config.EmbeddingProperties;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.List;

@Service
@ConditionalOnProperty(prefix = "embedding", name = "provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiEmbeddingClient implements EmbeddingClient {

    private final EmbeddingProperties properties;
    private final RestTemplate restTemplate;

    public GeminiEmbeddingClient(EmbeddingProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(20000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public List<Double> embed(String text) {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("Embedding is disabled");
        }
        if (!StringUtils.hasText(properties.getApiKey()) || !StringUtils.hasText(properties.getModel())) {
            throw new IllegalStateException("Embedding API key or model is missing");
        }
        if (!StringUtils.hasText(text)) {
            return List.of();
        }

        String url = String.format("%s/%s:embedContent?key=%s",
                properties.getEndpoint(),
                properties.getModel(),
                properties.getApiKey());

        EmbedRequest request = new EmbedRequest();
        request.setContent(new Content(List.of(new Part(text))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EmbedResponse response = restTemplate.postForObject(
                url,
                new HttpEntity<>(request, headers),
                EmbedResponse.class
        );

        if (response == null || response.getEmbedding() == null || response.getEmbedding().getValues() == null) {
            throw new IllegalStateException("Gemini embedding response is empty");
        }
        if (response.getEmbedding().getValues().isEmpty()) {
            throw new IllegalStateException("Gemini embedding vector is empty");
        }
        return response.getEmbedding().getValues();
    }

    @Data
    public static class EmbedRequest {
        private Content content;
    }

    @Data
    public static class Content {
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    @Data
    public static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }
    }

    @Data
    public static class EmbedResponse {
        private Embedding embedding;
    }

    @Data
    public static class Embedding {
        private List<Double> values;
    }
}
