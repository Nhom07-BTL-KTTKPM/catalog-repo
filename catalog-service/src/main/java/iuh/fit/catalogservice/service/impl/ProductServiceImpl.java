package iuh.fit.catalogservice.service.impl;

import iuh.fit.catalogservice.dto.request.ProductRequest;
import iuh.fit.catalogservice.dto.request.ProductImageRequest;
import iuh.fit.catalogservice.dto.request.ProductVariantRequest;
import iuh.fit.catalogservice.dto.request.ProductSoldUpdateRequest;
import iuh.fit.catalogservice.dto.response.ProductImageResponse;
import iuh.fit.catalogservice.dto.response.ProductResponse;
import iuh.fit.catalogservice.dto.response.ProductVariantResponse;
import iuh.fit.catalogservice.entity.Brand;
import iuh.fit.catalogservice.entity.Category;
import iuh.fit.catalogservice.entity.ProductImage;
import iuh.fit.catalogservice.entity.Product;
import iuh.fit.catalogservice.entity.ProductVariant;
import iuh.fit.catalogservice.repo.BrandRepository;
import iuh.fit.catalogservice.repo.CategoryRepository;
import iuh.fit.catalogservice.repo.ProductRepository;
import iuh.fit.catalogservice.repo.ProductVariantRepository;
import iuh.fit.catalogservice.service.ProductEmbeddingService;
import iuh.fit.catalogservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductVariantRepository productVariantRepository;
        private final ProductEmbeddingService productEmbeddingService;

        /**
         * Generate a unique slug from product name, handling duplicates
         */
        private String generateUniqueSlug(String baseSlug) {
                String slug = baseSlug;
                int counter = 1;
                while (productRepository.existsBySlug(slug)) {
                        slug = baseSlug + "-" + counter;
                        counter++;
                }
                return slug;
        }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
                log.info("Creating new product: {}", request.getName());

                if (request.getVariants() == null || request.getVariants().isEmpty()) {
                        throw new IllegalArgumentException("At least one product variant is required");
                }

                if (request.getImages() == null || request.getImages().isEmpty()) {
                        throw new IllegalArgumentException("At least one product image is required");
                }

                // Generate slug if not provided
                String slug = request.getSlug();
                if (slug == null || slug.isBlank()) {
                        slug = iuh.fit.catalogservice.util.SlugGenerator.generate(request.getName());
        }
                slug = generateUniqueSlug(slug);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.getCategoryId()));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with ID: " + request.getBrandId()));

        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .ingredients(request.getIngredients())
                .usageInstructions(request.getUsageInstructions())
                .suitableSkinTypes(request.getSuitableSkinTypes())
                .skinConcerns(request.getSkinConcerns())
                .isActive(request.getIsActive())
                .isFeatured(request.getIsFeatured())
                .category(category)
                .brand(brand)
                .build();

        applyImages(product, request.getImages());
        applyVariants(product, request.getVariants());
        updatePriceRange(product);

                Product savedProduct = productRepository.save(product);
        log.info("Created product with ID: {}", savedProduct.getProductId());

                productEmbeddingService.indexProduct(savedProduct);

        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

                // Update slug if provided, otherwise keep existing
                String slug = request.getSlug();
                if (slug == null || slug.isBlank()) {
                        slug = product.getSlug();  // Keep existing slug if not provided
                } else if (!product.getSlug().equals(slug)) {
                        // Generate unique slug if provided slug differs from current one
                        slug = generateUniqueSlug(slug);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.getCategoryId()));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with ID: " + request.getBrandId()));

        product.setName(request.getName());
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setIngredients(request.getIngredients());
        product.setUsageInstructions(request.getUsageInstructions());
        product.setSuitableSkinTypes(request.getSuitableSkinTypes());
        product.setSkinConcerns(request.getSkinConcerns());
        product.setIsActive(request.getIsActive());
        product.setIsFeatured(request.getIsFeatured());
        product.setCategory(category);
        product.setBrand(brand);

                Product updatedProduct = productRepository.save(product);
        log.info("Updated product with ID: {}", updatedProduct.getProductId());

                productEmbeddingService.indexProduct(updatedProduct);

        return mapToResponse(updatedProduct);
    }

        private void applyImages(Product product, List<ProductImageRequest> imageRequests) {
                if (imageRequests == null || imageRequests.isEmpty()) {
                        return;
                }

                imageRequests.forEach(imageRequest -> product.addImage(ProductImage.builder()
                                .url(imageRequest.getUrl())
                                .publicId(imageRequest.getPublicId())
                                .altText(imageRequest.getAltText())
                                .displayOrder(imageRequest.getDisplayOrder())
                                .isPrimary(Boolean.TRUE.equals(imageRequest.getIsPrimary()))
                                .build()));
        }

        private void applyVariants(Product product, List<ProductVariantRequest> variantRequests) {
                if (variantRequests == null || variantRequests.isEmpty()) {
                        return;
                }

                variantRequests.forEach(variantRequest -> product.addVariant(ProductVariant.builder()
                                .sku(variantRequest.getSku())
                                .variantName(variantRequest.getVariantName())
                                .price(variantRequest.getPrice())
                                .originalPrice(variantRequest.getOriginalPrice())
                                .stockQuantity(variantRequest.getStockQuantity())
                                .imageUrl(variantRequest.getImageUrl())
                                .isActive(variantRequest.getIsActive())
                                .build()));
        }

        private void updatePriceRange(Product product) {
                if (product.getVariants() == null || product.getVariants().isEmpty()) {
                        return;
                }

                BigDecimal minPrice = product.getVariants().stream()
                                .map(ProductVariant::getPrice)
                                .min(Comparator.naturalOrder())
                                .orElse(null);

                BigDecimal maxPrice = product.getVariants().stream()
                                .map(ProductVariant::getPrice)
                                .max(Comparator.naturalOrder())
                                .orElse(null);

                product.setMinPrice(minPrice);
                product.setMaxPrice(maxPrice);
        }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        log.debug("Fetching product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        log.debug("Fetching product with slug: {}", slug);

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with slug: " + slug));

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        log.debug("Fetching all active products with pagination");

        return productRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        log.debug("Fetching featured products");

        return productRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByTotalSoldDesc(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        log.debug("Fetching products for category ID: {}", categoryId);

        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByRootCategory(UUID rootCategoryId, Pageable pageable) {
        log.debug("Fetching products for root category ID: {} (including subcategories)", rootCategoryId);

        return productRepository.findByRootCategoryId(rootCategoryId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByBrand(UUID brandId, Pageable pageable) {
        log.debug("Fetching products for brand ID: {}", brandId);

        return productRepository.findByBrandIdAndIsActiveTrue(brandId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategoryAndBrand(UUID categoryId, UUID brandId, Pageable pageable) {
        log.debug("Fetching products for category ID: {} and brand ID: {}", categoryId, brandId);

        return productRepository.findByCategoryIdAndBrandIdAndIsActiveTrue(categoryId, brandId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        log.debug("Searching products with keyword: {}", keyword);

        return productRepository.searchProducts(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getBestSellingProducts(Pageable pageable) {
        log.debug("Fetching best-selling products");

        return productRepository.findBestSellingProducts(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getTopRatedProducts(Pageable pageable) {
        log.debug("Fetching top-rated products");

        return productRepository.findTopRatedProducts(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsBySkinType(String skinType, Pageable pageable) {
        log.debug("Fetching products for skin type: {}", skinType);

        return productRepository.findBySkinType(skinType, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsBySkinConcern(String skinConcern, Pageable pageable) {
        log.debug("Fetching products for skin concern: {}", skinConcern);

        return productRepository.findBySkinConcern(skinConcern, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        log.debug("Fetching products within price range: {} - {}", minPrice, maxPrice);

        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public void deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }

        productRepository.deleteById(id);
        log.info("Deleted product with ID: {}", id);
    }

    @Override
    public void updateProductPriceRange(UUID productId) {
        log.debug("Updating price range for product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        List<ProductVariant> variants = productVariantRepository.findByProductIdAndIsActiveTrue(productId);

        if (!variants.isEmpty()) {
            BigDecimal minPrice = variants.stream()
                    .map(ProductVariant::getPrice)
                    .min(Comparator.naturalOrder())
                    .orElse(null);

            BigDecimal maxPrice = variants.stream()
                    .map(ProductVariant::getPrice)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            product.setMinPrice(minPrice);
            product.setMaxPrice(maxPrice);
            productRepository.save(product);

            log.debug("Updated price range for product {}: {} - {}", productId, minPrice, maxPrice);
        }
    }

        @Override
        public void incrementTotalSold(List<ProductSoldUpdateRequest> requests) {
                if (requests == null || requests.isEmpty()) {
                        return;
                }

                Map<UUID, Integer> incrementByProductId = new HashMap<>();
                for (ProductSoldUpdateRequest request : requests) {
                        if (request == null || request.productId() == null) {
                                throw new IllegalArgumentException("productId is required");
                        }
                        if (request.quantity() == null || request.quantity() <= 0) {
                                throw new IllegalArgumentException("quantity must be greater than zero");
                        }

                        incrementByProductId.merge(request.productId(), request.quantity(), Integer::sum);
                }

                incrementByProductId.forEach((productId, quantity) -> {
                        Product product = productRepository.findById(productId)
                                        .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

                        int currentSold = product.getTotalSold() == null ? 0 : product.getTotalSold();
                        product.setTotalSold(currentSold + quantity);
                        productRepository.save(product);

                        log.debug("Incremented total sold for product {} by {}. New total: {}", productId, quantity, product.getTotalSold());
                });
        }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getProductId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .ingredients(product.getIngredients())
                .usageInstructions(product.getUsageInstructions())
                .suitableSkinTypes(product.getSuitableSkinTypes())
                .skinConcerns(product.getSkinConcerns())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .totalSold(product.getTotalSold())
                .minPrice(product.getMinPrice())
                .maxPrice(product.getMaxPrice())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())
                .brandLogoUrl(product.getBrand().getLogoUrl())
                .images(product.getImages().stream()
                        .map(this::mapImageToResponse)
                        .collect(Collectors.toList()))
                .variants(product.getVariants().stream()
                        .map(this::mapVariantToResponse)
                        .collect(Collectors.toList()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductImageResponse mapImageToResponse(iuh.fit.catalogservice.entity.ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct().getProductId())
                .url(image.getUrl())
                .publicId(image.getPublicId())
                .altText(image.getAltText())
                .displayOrder(image.getDisplayOrder())
                .isPrimary(image.getIsPrimary())
                .createdAt(image.getCreatedAt())
                .build();
    }

    private ProductVariantResponse mapVariantToResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getProductId())
                .sku(variant.getSku())
                .variantName(variant.getVariantName())
                .price(variant.getPrice())
                .originalPrice(variant.getOriginalPrice())
                .stockQuantity(variant.getStockQuantity())
                .sold(variant.getSold())
                .imageUrl(variant.getImageUrl())
                .isActive(variant.getIsActive())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}

