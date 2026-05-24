package iuh.fit.catalogservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import iuh.fit.catalogservice.dto.request.ProductRequest;
import iuh.fit.catalogservice.dto.request.ProductSoldUpdateRequest;
import iuh.fit.catalogservice.dto.request.ProductStatusRequest;
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
    ProductResponse getProductBySlug(String slug);

    /**
     * Get all active products with pagination
     */
    Page<ProductResponse> getAllActiveProducts(Pageable pageable);

    /**
     * Get all products for admin panel (includes inactive)
     */
    Page<ProductResponse> getAllProducts(Pageable pageable);

    /**
     * Get featured products
     */
    Page<ProductResponse> getFeaturedProducts(Pageable pageable);

    /**
     * Get products by category
     */
    Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable);

    /**
     * Get products by root category ID (includes all subcategories)
     */
    Page<ProductResponse> getProductsByRootCategory(UUID rootCategoryId, Pageable pageable);

    /**
     * Get products by brand
     */
    Page<ProductResponse> getProductsByBrand(UUID brandId, Pageable pageable);

    /**
     * Get products by category and brand
     */
    Page<ProductResponse> getProductsByCategoryAndBrand(UUID categoryId, UUID brandId, Pageable pageable);

    /**
     * Search products by slug or description
     */
    Page<ProductResponse> searchBySlugOrDescription(String keyword, Pageable pageable);

    /**
     * Get best-selling products
     */
    Page<ProductResponse> getBestSellingProducts(Pageable pageable);

    /**
     * Get top-rated products
     */
    Page<ProductResponse> getTopRatedProducts(Pageable pageable);

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
     * Delete product
     */
    void deleteProduct(UUID id);

    /**
     * Update product price range based on variants
     */
    void updateProductPriceRange(UUID productId);

    /**
     * Increment totalSold count for multiple products
     */
    void incrementTotalSold(List<ProductSoldUpdateRequest> requests);


}

