package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.response.EmbeddingReindexResponse;
import iuh.fit.catalogservice.service.EmbeddingReindexLockedException;
import iuh.fit.catalogservice.service.ProductEmbeddingService;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/catalog/embeddings")
public class InternalEmbeddingController {

    private final ProductEmbeddingService embeddingService;

    public InternalEmbeddingController(ProductEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping("/reindex")
    public ResponseEntity<EmbeddingReindexResponse> reindex() {
        try {
            ProductEmbeddingService.ReindexResult result = embeddingService.reindexAllProducts();
            EmbeddingReindexResponse response = EmbeddingReindexResponse.builder()
                    .processed(result.processed())
                    .failed(result.failed())
                    .startedAt(result.startedAt())
                    .finishedAt(result.finishedAt())
                    .build();
            return ResponseEntity.ok(response);
        } catch (EmbeddingReindexLockedException ex) {
            Instant now = Instant.now();
            EmbeddingReindexResponse response = EmbeddingReindexResponse.builder()
                    .processed(0)
                    .failed(0)
                    .startedAt(now)
                    .finishedAt(now)
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}
