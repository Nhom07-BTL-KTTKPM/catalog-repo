package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.request.StockAdjustmentRequest;
import iuh.fit.catalogservice.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/catalog/variants")
public class InternalProductVariantController {

    private final ProductVariantService productVariantService;

    public InternalProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @PostMapping("/stock-adjustments")
    public ResponseEntity<Void> applyStockAdjustment(@Valid @RequestBody StockAdjustmentRequest request) {
        productVariantService.applyStockAdjustment(request);
        return ResponseEntity.noContent().build();
    }
}
