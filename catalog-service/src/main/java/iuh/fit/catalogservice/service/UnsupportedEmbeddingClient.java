package iuh.fit.catalogservice.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnMissingBean(EmbeddingClient.class)
public class UnsupportedEmbeddingClient implements EmbeddingClient {

    @Override
    public List<Double> embed(String text) {
        throw new IllegalStateException("No embedding provider configured");
    }
}
