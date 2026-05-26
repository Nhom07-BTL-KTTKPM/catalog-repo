package iuh.fit.catalogservice.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for dynamic product filtering via query parameters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterRequest {

    private String keyword;

    private List<UUID> categoryIds;

    private List<UUID> brandIds;

    private List<String> skinTypes;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer rating;

    private List<String> promotions;
}