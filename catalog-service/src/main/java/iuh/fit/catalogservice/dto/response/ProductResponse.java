package iuh.fit.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for Product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String ingredients;
    private String usageInstructions;
    private List<String> suitableSkinTypes;
    private List<String> skinConcerns;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalSold;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isActive;
    private Boolean isFeatured;
    private UUID categoryId;
    private String categoryName;
    private UUID brandId;
    private String brandName;
    private String brandLogoUrl;
    private List<ProductImageResponse> images;
    private List<ProductVariantResponse> variants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

