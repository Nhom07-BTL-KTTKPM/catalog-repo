package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.request.SemanticSearchRequest;
import iuh.fit.catalogservice.dto.response.SemanticSearchItem;
import iuh.fit.catalogservice.dto.response.SemanticSearchResponse;
import iuh.fit.catalogservice.service.SemanticSearchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/catalog")
public class InternalSearchController {

    private final SemanticSearchService semanticSearchService;

    public InternalSearchController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @PostMapping("/semantic-search")
    public ResponseEntity<SemanticSearchResponse> semanticSearch(
            @Valid @RequestBody SemanticSearchRequest request
    ) {
        int topK = request.getTopK() == null ? 5 : request.getTopK();
        List<SemanticSearchItem> items = semanticSearchService.search(
                request.getEmbedding(),
                topK,
                request.getMinScore(),
                request.getQueryText()
        );
        return ResponseEntity.ok(SemanticSearchResponse.builder().items(items).build());
    }
}
