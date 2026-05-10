package iuh.fit.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for ProductImage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {
    private UUID id;
    private UUID productId;
    private String url;
    private String publicId;
    private String altText;
    private Integer displayOrder;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}

