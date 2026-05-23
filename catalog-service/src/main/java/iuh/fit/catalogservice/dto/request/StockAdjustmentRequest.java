package iuh.fit.catalogservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record StockAdjustmentRequest(
        @NotEmpty List<@Valid StockAdjustmentItem> items
) {
    public record StockAdjustmentItem(
            @NotNull UUID productVariantId,
            @NotNull @Positive Integer quantity
    ) {
    }
}
