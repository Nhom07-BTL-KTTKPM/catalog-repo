package iuh.fit.catalogservice.service.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iuh.fit.catalogservice.dto.request.ProductImageRequest;
import iuh.fit.catalogservice.dto.request.ProductRequest;
import iuh.fit.catalogservice.dto.request.ProductSoldUpdateRequest;
import iuh.fit.catalogservice.dto.request.ProductStatusRequest;
import iuh.fit.catalogservice.dto.request.ProductVariantRequest;
import iuh.fit.catalogservice.dto.response.ProductCardResponse;
import iuh.fit.catalogservice.dto.response.ProductImageResponse;
import iuh.fit.catalogservice.dto.response.ProductResponse;
import iuh.fit.catalogservice.dto.response.ProductVariantResponse;
import iuh.fit.catalogservice.entity.Brand;
import iuh.fit.catalogservice.entity.Category;
import iuh.fit.catalogservice.entity.Product;
import iuh.fit.catalogservice.entity.ProductImage;
import iuh.fit.catalogservice.entity.ProductVariant;
import iuh.fit.catalogservice.repo.BrandRepository;
import iuh.fit.catalogservice.repo.CategoryRepository;
import iuh.fit.catalogservice.repo.ProductRepository;
import iuh.fit.catalogservice.repo.ProductVariantRepository;
import iuh.fit.catalogservice.service.ProductEmbeddingService;
import iuh.fit.catalogservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

                validateProductRequest(request);

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

                Product savedProduct = productRepository.saveAndFlush(product);
        log.info("Created product with ID: {}", savedProduct.getProductId());

                productEmbeddingService.indexProduct(savedProduct);

        return mapToResponse(savedProduct);
    }

        

    @Override
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);

        validateProductRequest(request);

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
        replaceImages(product, request.getImages());
        syncVariants(product, request.getVariants());
        updatePriceRange(product);

                Product updatedProduct = productRepository.saveAndFlush(product);
        log.info("Updated product with ID: {}", updatedProduct.getProductId());

                productEmbeddingService.indexProduct(updatedProduct);

        return mapToResponse(updatedProduct);
    }

        @Override
        public ProductResponse updateProductIsActive(UUID id, ProductStatusRequest request) {
                log.info("Updating product isActive status with ID: {}", id);

                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

                product.setIsActive(request.getIsActive());

                Product updatedProduct = productRepository.save(product);
                log.info("Updated product isActive status with ID: {}", updatedProduct.getProductId());

                productEmbeddingService.indexProduct(updatedProduct);

                return mapToResponse(updatedProduct);
        }

        private void validateProductRequest(ProductRequest request) {
                if (request.getVariants() == null || request.getVariants().isEmpty()) {
                        throw new IllegalArgumentException("At least one product variant is required");
                }

                if (request.getImages() == null || request.getImages().isEmpty()) {
                        throw new IllegalArgumentException("At least one product image is required");
                }

                Set<String> seenSkus = new HashSet<>();
                for (ProductVariantRequest variant : request.getVariants()) {
                        String normalizedSku = variant.getSku() == null ? null : variant.getSku().trim().toLowerCase();
                        if (normalizedSku == null || normalizedSku.isEmpty()) {
                                continue;
                        }
                        if (!seenSkus.add(normalizedSku)) {
                                throw new IllegalArgumentException("Duplicate variant SKU in request: " + variant.getSku());
                        }
                }
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

        private void replaceImages(Product product, List<ProductImageRequest> imageRequests) {
                Iterator<ProductImage> iterator = product.getImages().iterator();
                while (iterator.hasNext()) {
                        ProductImage image = iterator.next();
                        iterator.remove();
                        image.setProduct(null);
                }
                applyImages(product, imageRequests);
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

        private void syncVariants(Product product, List<ProductVariantRequest> variantRequests) {
                Map<String, ProductVariant> existingVariantsBySku = product.getVariants().stream()
                                .collect(Collectors.toMap(
                                                variant -> normalizeSku(variant.getSku()),
                                                Function.identity()));

                Set<String> requestedSkus = new HashSet<>();
                for (ProductVariantRequest request : variantRequests) {
                        String normalizedSku = normalizeSku(request.getSku());
                        requestedSkus.add(normalizedSku);

                        ProductVariant existingVariant = existingVariantsBySku.get(normalizedSku);
                        if (existingVariant != null) {
                                existingVariant.setVariantName(request.getVariantName());
                                existingVariant.setPrice(request.getPrice());
                                existingVariant.setOriginalPrice(request.getOriginalPrice());
                                existingVariant.setStockQuantity(request.getStockQuantity());
                                existingVariant.setImageUrl(request.getImageUrl());
                                existingVariant.setIsActive(request.getIsActive());
                                continue;
                        }

                        product.addVariant(ProductVariant.builder()
                                        .sku(request.getSku())
                                        .variantName(request.getVariantName())
                                        .price(request.getPrice())
                                        .originalPrice(request.getOriginalPrice())
                                        .stockQuantity(request.getStockQuantity())
                                        .imageUrl(request.getImageUrl())
                                        .isActive(request.getIsActive())
                                        .build());
                }

                Iterator<ProductVariant> iterator = product.getVariants().iterator();
                while (iterator.hasNext()) {
                        ProductVariant variant = iterator.next();
                        if (!requestedSkus.contains(normalizeSku(variant.getSku()))) {
                                iterator.remove();
                                variant.setProduct(null);
                        }
                }
        }

        private String normalizeSku(String sku) {
                return sku == null ? "" : sku.trim().toLowerCase();
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
        public ProductCardResponse getProductBySlug(String slug) {
        log.debug("Fetching product with slug: {}", slug);

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with slug: " + slug));

                return mapToCardResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getAllActiveProducts(Pageable pageable) {
        log.debug("Fetching all active products with pagination");

        return productRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                                .map(this::mapToCardResponse);
    }

        @Override
        @Transactional(readOnly = true)
        public Page<ProductResponse> getAllProducts(Pageable pageable) {
                log.debug("Fetching all products for admin panel");
                return productRepository.findAllWithBrandAndCategory(pageable)
                                .map(this::mapToResponse);
        }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getFeaturedProducts(Pageable pageable) {
        log.debug("Fetching featured products");

        return productRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByTotalSoldDesc(pageable)
                .map(this::mapToCardResponse);
    }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getProductsByCategory(UUID categoryId, Pageable pageable) {
        log.debug("Fetching products for category ID: {}", categoryId);

        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                                .map(this::mapToCardResponse);
    }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getProductsByRootCategory(UUID rootCategoryId, Pageable pageable) {
        log.debug("Fetching products for root category ID: {} (including subcategories)", rootCategoryId);

        return productRepository.findByRootCategoryId(rootCategoryId, pageable)
                                .map(this::mapToCardResponse);
    }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getProductsByBrand(UUID brandId, Pageable pageable) {
        log.debug("Fetching products for brand ID: {}", brandId);

        return productRepository.findByBrandIdAndIsActiveTrue(brandId, pageable)
                                .map(this::mapToCardResponse);
    }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getProductsByCategoryAndBrand(UUID categoryId, UUID brandId, Pageable pageable) {
        log.debug("Fetching products for category ID: {} and brand ID: {}", categoryId, brandId);

        return productRepository.findByCategoryIdAndBrandIdAndIsActiveTrue(categoryId, brandId, pageable)
                                .map(this::mapToCardResponse);
    }

        @Override
        @Transactional(readOnly = true)
        public Page<ProductCardResponse> searchBySlugOrDescription(String keyword, Pageable pageable) {
                log.debug("Searching products by slug or description with keyword: {}", keyword);

                return productRepository.searchBySlugOrDescription(keyword, pageable)
                                .map(this::mapToCardResponse);
        }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getBestSellingProducts(Pageable pageable) {
        log.debug("Fetching best-selling products");

        return productRepository.findBestSellingProducts(pageable)
                                .map(this::mapToCardResponse);
    }

    @Override
    @Transactional(readOnly = true)
        public Page<ProductCardResponse> getTopRatedProducts(Pageable pageable) {
        log.debug("Fetching top-rated products");

        return productRepository.findTopRatedProducts(pageable)
                                .map(this::mapToCardResponse);
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

        // 1. Grouping và Strict Validation (Lấy từ code 2)
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

        // 2. Fetch data hàng loạt (Lấy từ code 1)
        List<Product> products = productRepository.findAllById(incrementByProductId.keySet());

        // Kiểm tra xem có Product ID nào truyền vào mà không tồn tại trong DB không
        if (products.size() < incrementByProductId.size()) {
                throw new IllegalArgumentException("One or more products were not found in the database.");
        }

        // 3. Cập nhật data và ghi log 
        for (Product product : products) {
                Integer quantityToAdd = incrementByProductId.get(product.getProductId());
                Integer currentSold = product.getTotalSold();
                
                product.setTotalSold((currentSold == null ? 0 : currentSold) + quantityToAdd);
                
                log.debug("Incremented total sold for product {} by {}. New total: {}", 
                          product.getProductId(), quantityToAdd, product.getTotalSold());
        }

        // 4. Save hàng loạt (Lấy từ code 1)
        productRepository.saveAll(products);
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

        private ProductCardResponse mapToCardResponse(Product product) {
                return ProductCardResponse.builder()
                                .id(product.getProductId())
                                .name(product.getName())
                                .slug(product.getSlug())
                                .averageRating(product.getAverageRating())
                                .totalSold(product.getTotalSold())
                                .minPrice(product.getMinPrice())
                                .maxPrice(product.getMaxPrice())
                                .isFeatured(product.getIsFeatured())
                                .thumbnail(resolveThumbnail(product))
                                .build();
        }

        private String resolveThumbnail(Product product) {
                if (product.getImages() == null || product.getImages().isEmpty()) {
                        return null;
                }

                return product.getImages().stream()
                                .filter(image -> Boolean.TRUE.equals(image.getIsPrimary()))
                                .map(ProductImage::getUrl)
                                .findFirst()
                                .orElseGet(() -> product.getImages().get(0).getUrl());
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
