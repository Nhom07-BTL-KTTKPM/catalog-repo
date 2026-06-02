package iuh.fit.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary numbers for the admin product overview cards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOverviewResponse {
    private long totalProducts;
    private long activeProducts;
    private long featuredProducts;
    private long outOfStockProducts;
}