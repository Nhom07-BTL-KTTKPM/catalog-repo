package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.response.ProductVariantResponse;
import iuh.fit.catalogservice.service.ProductVariantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog/variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<ProductVariantResponse> getVariantById(@PathVariable UUID variantId) {
        return ResponseEntity.ok(productVariantService.getVariantById(variantId));
    }
}
