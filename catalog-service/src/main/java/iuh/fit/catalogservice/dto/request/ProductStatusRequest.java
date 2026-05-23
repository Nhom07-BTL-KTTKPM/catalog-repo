package iuh.fit.catalogservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating product active status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatusRequest {

    @NotNull(message = "isActive field is required")
    @Builder.Default
    private Boolean isActive = true;
}