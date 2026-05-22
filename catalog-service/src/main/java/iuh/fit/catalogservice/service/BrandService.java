package iuh.fit.catalogservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import iuh.fit.catalogservice.dto.request.BrandRequest;
import iuh.fit.catalogservice.dto.request.BrandStatusRequest;
import iuh.fit.catalogservice.dto.response.BrandResponse;
import iuh.fit.catalogservice.dto.response.BrandSummaryResponse;

/**
 * Service interface for Brand operations
 */
public interface BrandService {

    /**
     * Create a new brand
     */
    BrandResponse createBrand(BrandRequest request);

    /**
     * Update an existing brand
     */
    BrandResponse updateBrand(UUID id, BrandRequest request);

    /**
     * Get brand by ID
     */
    BrandResponse getBrandById(UUID id);

    /**
     * Get brand by slug
     */
    BrandResponse getBrandBySlug(String slug);

    /**
     * Get all brands with pagination
     */
    Page<BrandResponse> getAllBrands(Pageable pageable);

    /**
     * Get all active brands
     */
    List<BrandResponse> getActiveBrands();

    /**
      * Update brand active status
     */
     BrandResponse updateBrandIsActive(UUID id, BrandStatusRequest request);

    /**
     * Search brands by name
     */
    List<BrandResponse> searchBrands(String keyword);

    /**
     * Get brands by origin country
     */
    List<BrandResponse> getBrandsByOriginCountry(String originCountry);

    /**
     * Get lightweight summaries (id, name, slug, logoUrl) of active brands.
     * Phục vụ hiển thị nhanh danh sách brand trên giao diện (homepage, filter...).
     */
    List<BrandSummaryResponse> getActiveBrandSummaries();
}

