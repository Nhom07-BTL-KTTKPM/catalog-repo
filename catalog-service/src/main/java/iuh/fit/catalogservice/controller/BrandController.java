package iuh.fit.catalogservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iuh.fit.catalogservice.dto.request.BrandRequest;
import iuh.fit.catalogservice.dto.request.BrandStatusRequest;
import iuh.fit.catalogservice.dto.response.BrandResponse;
import iuh.fit.catalogservice.dto.response.BrandSummaryResponse;
import iuh.fit.catalogservice.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Brand management
 */
@RestController
@RequestMapping("/api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable UUID id,
            @Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable UUID id) {
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BrandResponse> getBrandBySlug(@PathVariable String slug) {
        BrandResponse response = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<BrandResponse>> getAllBrands(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BrandResponse> response = brandService.getAllBrands(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BrandResponse>> getActiveBrands() {
        List<BrandResponse> response = brandService.getActiveBrands();
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách rút gọn của các thương hiệu đang hoạt động.
     * Trả về (id, name, slug, logoUrl) để hiển thị danh sách brand trên giao diện.
     */
    @GetMapping("/summary")
    public ResponseEntity<List<BrandSummaryResponse>> getActiveBrandSummaries() {
        List<BrandSummaryResponse> response = brandService.getActiveBrandSummaries();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BrandResponse> updateBrandIsActive(
            @PathVariable UUID id,
            @Valid @RequestBody BrandStatusRequest request) {
        BrandResponse response = brandService.updateBrandIsActive(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BrandResponse>> searchBrands(@RequestParam String keyword) {
        List<BrandResponse> response = brandService.searchBrands(keyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<BrandResponse>> getBrandsByOriginCountry(@PathVariable String country) {
        List<BrandResponse> response = brandService.getBrandsByOriginCountry(country);
        return ResponseEntity.ok(response);
    }
}

