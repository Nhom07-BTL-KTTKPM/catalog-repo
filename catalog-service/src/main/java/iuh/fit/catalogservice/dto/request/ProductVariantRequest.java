package iuh.fit.catalogservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating/updating ProductVariant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @NotBlank(message = "Variant name is required")
    @Size(max = 255, message = "Variant name must not exceed 255 characters")
    private String variantName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Original price must be greater than 0")
    private BigDecimal originalPrice;

    @NotNull(message = "Stock quantity is required")
    private Integer stockQuantity;

    private String imageUrl;

    @Builder.Default
    private Boolean isActive = true;
}

