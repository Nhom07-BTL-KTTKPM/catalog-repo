package iuh.fit.catalogservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight product response for listing/search pages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCardResponse {
    private UUID id;
    private String name;
    private String slug;
    private Double averageRating;
    private Integer totalSold;
    private Integer totalStock;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isActive;
    private Boolean isFeatured;
    private String thumbnail;
}