package iuh.fit.catalogservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import iuh.fit.catalogservice.dto.request.ProductFilterRequest;
import iuh.fit.catalogservice.dto.request.ProductRequest;
import iuh.fit.catalogservice.dto.request.ProductSoldUpdateRequest;
import iuh.fit.catalogservice.dto.request.ProductStatusRequest;
import iuh.fit.catalogservice.dto.response.ProductCardResponse;
import iuh.fit.catalogservice.dto.response.ProductResponse;

/**
 * Service interface for Product operations
 */
public interface ProductService {

    /**
     * Create a new product
     */
    ProductResponse createProduct(ProductRequest request);

    /**
     * Update an existing product
     */
    ProductResponse updateProduct(UUID id, ProductRequest request);

    /**
     * Update product active status.
     */
    ProductResponse updateProductIsActive(UUID id, ProductStatusRequest request);

    /**
     * Get product by ID
     */
    ProductResponse getProductById(UUID id);

    /**
     * Get product by slug
     */
    ProductCardResponse getProductBySlug(String slug);

    /**
     * Get all active products with pagination
     */
    Page<ProductCardResponse> getAllActiveProducts(Pageable pageable);

    /**
     * Get all products for admin panel (includes inactive)
     */
    Page<ProductCardResponse> getAllProducts(Pageable pageable);

    /**
     * Get featured products
     */
    Page<ProductCardResponse> getFeaturedProducts(Pageable pageable);

    /**
     * Get products by category
     */
    Page<ProductCardResponse> getProductsByCategory(UUID categoryId, Pageable pageable);

    /**
     * Get products by root category ID (includes all subcategories)
     */
    Page<ProductCardResponse> getProductsByRootCategory(UUID rootCategoryId, Pageable pageable);

    /**
     * Get products by brand
     */
    Page<ProductCardResponse> getProductsByBrand(UUID brandId, Pageable pageable);

    /**
     * Get products by category and brand
     */
    Page<ProductCardResponse> getProductsByCategoryAndBrand(UUID categoryId, UUID brandId, Pageable pageable);

    /**
     * Search products by slug or description
     */
    Page<ProductCardResponse> searchBySlugOrDescription(String keyword, Pageable pageable);

    /**
     * Filter products dynamically by query parameters.
     */
    Page<ProductCardResponse> filterProducts(ProductFilterRequest filterRequest, Pageable pageable);

    /**
     * Get best-selling products
     */
    Page<ProductCardResponse> getBestSellingProducts(Pageable pageable);

    /**
     * Get top-rated products
     */
    Page<ProductCardResponse> getTopRatedProducts(Pageable pageable);

    /**
     * Get products by skin type
     */
    Page<ProductResponse> getProductsBySkinType(String skinType, Pageable pageable);

    /**
     * Get products by skin concern
     */
    Page<ProductResponse> getProductsBySkinConcern(String skinConcern, Pageable pageable);

    /**
     * Get products within price range
     */
    Page<ProductResponse> getProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);

    /**
     * Update product price range based on variants
     */
    void updateProductPriceRange(UUID productId);

    /**
     * Increment totalSold count for multiple products
     */
    void incrementTotalSold(List<ProductSoldUpdateRequest> requests);
}
