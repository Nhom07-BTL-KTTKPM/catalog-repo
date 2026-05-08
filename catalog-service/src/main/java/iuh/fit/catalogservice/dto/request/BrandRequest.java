package iuh.fit.catalogservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating Brand
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 255, message = "Brand name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Brand slug is required")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    private String description;

    private String logoUrl;

    @Size(max = 100, message = "Origin country must not exceed 100 characters")
    private String originCountry;

    private String websiteUrl;

    @NotNull(message = "isActive field is required")
    @Builder.Default
    private Boolean isActive = true;
}

