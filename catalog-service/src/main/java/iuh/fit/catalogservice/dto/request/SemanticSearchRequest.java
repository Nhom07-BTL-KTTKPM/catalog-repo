package iuh.fit.catalogservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanticSearchRequest {

    @NotNull
    @NotEmpty
    private List<Double> embedding;

    @Min(1)
    @Max(50)
    private Integer topK;
}
