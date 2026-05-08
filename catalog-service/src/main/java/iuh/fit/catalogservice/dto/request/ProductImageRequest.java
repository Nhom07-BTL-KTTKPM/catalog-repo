package iuh.fit.catalogservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating/updating ProductImage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Image URL is required")
    private String url;

    private String publicId;

    private String altText;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isPrimary = false;
}

