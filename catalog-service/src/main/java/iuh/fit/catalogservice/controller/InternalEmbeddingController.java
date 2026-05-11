package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.response.EmbeddingReindexResponse;
import iuh.fit.catalogservice.service.ProductEmbeddingService;
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
        ProductEmbeddingService.ReindexResult result = embeddingService.reindexAllProducts();
        EmbeddingReindexResponse response = EmbeddingReindexResponse.builder()
                .processed(result.processed())
                .failed(result.failed())
                .startedAt(result.startedAt())
                .finishedAt(result.finishedAt())
                .build();
        return ResponseEntity.ok(response);
    }
}
