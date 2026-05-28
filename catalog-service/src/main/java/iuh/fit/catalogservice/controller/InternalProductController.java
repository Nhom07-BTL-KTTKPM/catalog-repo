package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.response.ProductResponse;
import iuh.fit.catalogservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/catalog/products")
public class InternalProductController {

    private final ProductService productService;

    public InternalProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getFullProductsByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 30) Pageable pageable) {
        Page<ProductResponse> response = productService.getFullProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<Page<ProductResponse>> getFullProductsByBrand(
            @PathVariable UUID brandId,
            @PageableDefault(size = 30) Pageable pageable) {
        Page<ProductResponse> response = productService.getFullProductsByBrand(brandId, pageable);
        return ResponseEntity.ok(response);
    }
}
