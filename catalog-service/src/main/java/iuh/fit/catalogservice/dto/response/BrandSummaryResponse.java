package iuh.fit.catalogservice.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandSummaryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String logoUrl;
}
