package iuh.fit.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Brand
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String originCountry;
    private String websiteUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

