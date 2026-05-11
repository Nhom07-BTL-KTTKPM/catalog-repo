package iuh.fit.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingReindexResponse {

    private int processed;
    private int failed;
    private Instant startedAt;
    private Instant finishedAt;
}
