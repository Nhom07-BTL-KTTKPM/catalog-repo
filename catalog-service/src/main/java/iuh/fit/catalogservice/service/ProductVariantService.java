package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.dto.request.StockAdjustmentRequest;
import iuh.fit.catalogservice.dto.response.ProductVariantResponse;

import java.util.UUID;

public interface ProductVariantService {
    ProductVariantResponse getVariantById(UUID variantId);

    void applyStockAdjustment(StockAdjustmentRequest request);
}
