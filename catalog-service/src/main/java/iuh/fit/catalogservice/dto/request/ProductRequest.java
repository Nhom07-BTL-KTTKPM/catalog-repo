package iuh.fit.catalogservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating/updating Product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 500, message = "Product name must not exceed 500 characters")
    private String name;

    @NotBlank(message = "Product slug is required")
    @Size(max = 500, message = "Slug must not exceed 500 characters")
    private String slug;

    private String description;

    private String ingredients;

    private String usageInstructions;

    private List<String> suitableSkinTypes;

    private List<String> skinConcerns;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Brand ID is required")
    private UUID brandId;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isFeatured = false;
}

