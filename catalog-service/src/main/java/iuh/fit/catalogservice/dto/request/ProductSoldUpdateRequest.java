package iuh.fit.catalogservice.dto.request;

import java.util.UUID;

public record ProductSoldUpdateRequest(
        UUID productId,
        Integer quantity
) {
}
