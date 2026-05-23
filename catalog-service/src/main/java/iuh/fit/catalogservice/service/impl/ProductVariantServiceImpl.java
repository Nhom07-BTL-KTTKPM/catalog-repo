package iuh.fit.catalogservice.service.impl;

import iuh.fit.catalogservice.dto.request.StockAdjustmentRequest;
import iuh.fit.catalogservice.dto.response.ProductVariantResponse;
import iuh.fit.catalogservice.entity.Product;
import iuh.fit.catalogservice.entity.ProductVariant;
import iuh.fit.catalogservice.repo.ProductVariantRepository;
import iuh.fit.catalogservice.service.ProductVariantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return mapToResponse(variant);
    }

    @Override
    @Transactional
    public void applyStockAdjustment(StockAdjustmentRequest request) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Stock adjustment items are required");
        }

        for (StockAdjustmentRequest.StockAdjustmentItem item : request.items()) {
            if (item.productVariantId() == null) {
                throw new IllegalArgumentException("productVariantId is required");
            }
            if (item.quantity() == null || item.quantity() <= 0) {
                throw new IllegalArgumentException("quantity must be greater than 0");
            }

            ProductVariant variant = productVariantRepository.findByIdForUpdate(item.productVariantId())
                    .orElseThrow(() -> new IllegalArgumentException("Product variant not found"));
            int currentStock = safeNumber(variant.getStockQuantity());
            int purchasedQuantity = item.quantity();
            if (currentStock < purchasedQuantity) {
                throw new IllegalArgumentException("Insufficient stock for product variant");
            }

            variant.setStockQuantity(currentStock - purchasedQuantity);
            variant.setSold(safeNumber(variant.getSold()) + purchasedQuantity);

            Product product = variant.getProduct();
            if (product != null) {
                product.setTotalSold(safeNumber(product.getTotalSold()) + purchasedQuantity);
            }
        }
    }

    private ProductVariantResponse mapToResponse(ProductVariant variant) {
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

    private int safeNumber(Integer value) {
        return value == null ? 0 : value;
    }
}
