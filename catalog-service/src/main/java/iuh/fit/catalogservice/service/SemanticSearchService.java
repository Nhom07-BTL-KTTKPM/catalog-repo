package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.dto.response.SemanticSearchItem;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SemanticSearchService {

    private final JdbcTemplate jdbcTemplate;

    public SemanticSearchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SemanticSearchItem> search(List<Double> embedding, int topK) {
        if (embedding == null || embedding.isEmpty()) {
            return List.of();
        }

        String vectorLiteral = toVectorLiteral(embedding);
        PGobject pgVector = toPgVector(vectorLiteral);

        String sql = "SELECT p.product_id, p.name, p.ingredients, p.usage_instructions, "
                + "p.suitable_skin_types, p.skin_concerns, p.min_price, p.max_price, "
                + "(1 - (e.embedding <=> ?::vector)) AS score "
                + "FROM product_embeddings e "
                + "JOIN products p ON p.product_id = e.product_id "
                + "ORDER BY e.embedding <=> ?::vector "
                + "LIMIT ?";

        return jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setObject(1, pgVector);
                    ps.setObject(2, pgVector);
                    ps.setInt(3, topK);
                },
                this::mapRow
        );
    }

    private SemanticSearchItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SemanticSearchItem.builder()
                .productId(UUID.fromString(rs.getString("product_id")))
                .name(rs.getString("name"))
                .ingredients(rs.getString("ingredients"))
                .usageInstructions(rs.getString("usage_instructions"))
                .suitableSkinTypes(splitList(rs.getString("suitable_skin_types")))
                .skinConcerns(splitList(rs.getString("skin_concerns")))
                .minPrice(rs.getBigDecimal("min_price"))
                .maxPrice(rs.getBigDecimal("max_price"))
                .score(rs.getDouble("score"))
                .build();
    }

    private PGobject toPgVector(String vectorLiteral) {
        PGobject pgObject = new PGobject();
        pgObject.setType("vector");
        try {
            pgObject.setValue(vectorLiteral);
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Invalid embedding vector format", ex);
        }
        return pgObject;
    }

    private String toVectorLiteral(List<Double> embedding) {
        return embedding.stream()
                .map(value -> String.format(Locale.ROOT, "%s", value))
                .collect(Collectors.joining(",", "[", "]"));
    }

    private List<String> splitList(String value) {
        if (!StringUtils.hasText(value)) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }
}
