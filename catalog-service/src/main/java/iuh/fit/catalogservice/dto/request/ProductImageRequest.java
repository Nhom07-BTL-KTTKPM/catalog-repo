package iuh.fit.catalogservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating ProductImage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {

    @NotBlank(message = "Image URL is required")
    private String url;

    private String publicId;

    private String altText;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isPrimary = false;
}

