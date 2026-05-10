package iuh.fit.catalogservice.controller;

import iuh.fit.catalogservice.dto.request.ProductRequest;
import iuh.fit.catalogservice.dto.response.ProductResponse;
import iuh.fit.catalogservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Product management
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        ProductResponse response = productService.getProductBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllActiveProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getAllActiveProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    public ResponseEntity<Page<ProductResponse>> getFeaturedProducts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponse> response = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByBrand(
            @PathVariable UUID brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getProductsByBrand(brandId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}/brand/{brandId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategoryAndBrand(
            @PathVariable UUID categoryId,
            @PathVariable UUID brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getProductsByCategoryAndBrand(categoryId, brandId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/best-selling")
    public ResponseEntity<Page<ProductResponse>> getBestSellingProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getBestSellingProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Page<ProductResponse>> getTopRatedProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getTopRatedProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/skin-type/{skinType}")
    public ResponseEntity<Page<ProductResponse>> getProductsBySkinType(
            @PathVariable String skinType,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getProductsBySkinType(skinType, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/skin-concern/{skinConcern}")
    public ResponseEntity<Page<ProductResponse>> getProductsBySkinConcern(
            @PathVariable String skinConcern,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getProductsBySkinConcern(skinConcern, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/price-range")
    public ResponseEntity<Page<ProductResponse>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductResponse> response = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update-price-range")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProductPriceRange(@PathVariable UUID id) {
        productService.updateProductPriceRange(id);
        return ResponseEntity.ok().build();
    }
}

