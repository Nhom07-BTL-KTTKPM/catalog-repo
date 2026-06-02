package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.config.EmbeddingProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Service
public class OllamaEmbeddingClient implements EmbeddingClient {

    private final EmbeddingProperties properties;
    private final RestTemplate restTemplate;

    public OllamaEmbeddingClient(EmbeddingProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        Duration timeout = properties.getOllama() == null ? null : properties.getOllama().getTimeout();
        int readTimeoutMs = timeout == null ? 20000 : Math.toIntExact(timeout.toMillis());
        factory.setReadTimeout(readTimeoutMs);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public List<Double> embed(String text) {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("Embedding is disabled");
        }
        EmbeddingProperties.Ollama ollama = properties.getOllama();
        if (ollama == null || !StringUtils.hasText(ollama.getBaseUrl()) || !StringUtils.hasText(ollama.getModel())) {
            throw new IllegalStateException("Ollama embedding base-url or model is missing");
        }
        if (!StringUtils.hasText(text)) {
            return List.of();
        }

        String url = normalizeBaseUrl(ollama.getBaseUrl()) + "/api/embeddings";
        EmbedRequest request = new EmbedRequest();
        request.setModel(ollama.getModel());
        request.setPrompt(text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EmbedResponse response = restTemplate.postForObject(
                url,
                new HttpEntity<>(request, headers),
                EmbedResponse.class
        );

        if (response == null || response.getEmbedding() == null) {
            throw new IllegalStateException("Ollama embedding response is empty");
        }
        if (response.getEmbedding().isEmpty()) {
            throw new IllegalStateException("Ollama embedding vector is empty");
        }
        validateDimension(response.getEmbedding());
        return response.getEmbedding();
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            return "http://localhost:11434";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private void validateDimension(List<Double> embedding) {
        int expected = properties.getVectorDimension();
        if (expected > 0 && embedding.size() != expected) {
            throw new IllegalStateException(
                    "Unexpected Ollama embedding dimension: expected=" + expected + ", actual=" + embedding.size()
            );
        }
    }

    public static class EmbedRequest {
        private String model;
        private String prompt;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }

    public static class EmbedResponse {
        private List<Double> embedding;

        public List<Double> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Double> embedding) {
            this.embedding = embedding;
        }
    }
}
