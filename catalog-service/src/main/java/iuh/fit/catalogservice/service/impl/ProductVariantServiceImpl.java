package iuh.fit.catalogservice.service.impl;

import iuh.fit.catalogservice.dto.response.ProductVariantResponse;
import iuh.fit.catalogservice.entity.ProductVariant;
import iuh.fit.catalogservice.repo.ProductVariantRepository;
import iuh.fit.catalogservice.service.ProductVariantService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;

    public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository) {
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public ProductVariantResponse getVariantById(UUID variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Product variant not found"));

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
