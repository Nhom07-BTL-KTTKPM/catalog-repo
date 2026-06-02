package iuh.fit.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for ProductVariant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    private UUID id;
    private UUID productId;
    private String sku;
    private String variantName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer sold;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

