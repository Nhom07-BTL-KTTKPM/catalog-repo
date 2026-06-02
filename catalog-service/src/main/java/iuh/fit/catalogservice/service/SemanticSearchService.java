package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.config.EmbeddingProperties;
import iuh.fit.catalogservice.dto.response.SemanticSearchItem;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SemanticSearchService {
    private static final int RERANK_CANDIDATE_MULTIPLIER = 3;
    private static final double VECTOR_SCORE_WEIGHT = 0.90d;
    private static final double LEXICAL_SCORE_WEIGHT = 0.10d;

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingProperties properties;

    public SemanticSearchService(JdbcTemplate jdbcTemplate, EmbeddingProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    public List<SemanticSearchItem> search(List<Double> embedding, int topK, Double minScoreOverride, String queryText) {
        if (embedding == null || embedding.isEmpty()) {
            return List.of();
        }
        validateDimension(embedding);
        int safeTopK = Math.max(1, Math.min(topK, 50));
        int candidateTopK = Math.max(safeTopK, Math.min(50, safeTopK * RERANK_CANDIDATE_MULTIPLIER));
        double minScore = resolveMinScore(minScoreOverride);

        String vectorLiteral = toVectorLiteral(embedding);
        PGobject pgVector = toPgVector(vectorLiteral);

        String sql = "SELECT p.product_id, p.name, p.description, p.ingredients, p.usage_instructions, "
            + "c.name AS category_name, b.name AS brand_name, "
            + "p.suitable_skin_types, p.skin_concerns, p.min_price, p.max_price, "
            + weightedScoreSql() + " AS score "
            + "FROM product_embeddings e "
            + "JOIN products p ON p.product_id = e.product_id "
            + "LEFT JOIN categories c ON c.id = p.category_id "
            + "LEFT JOIN brands b ON b.id = p.brand_id "
            + "WHERE p.is_active = true "
            + "AND e.embedding IS NOT NULL "
            + "AND " + weightedScoreSql() + " >= ? "
            + "ORDER BY score DESC "
            + "LIMIT ?";

        List<SemanticSearchItem> rawItems = jdbcTemplate.query(
                sql,
                ps -> {
                    for (int i = 1; i <= 10; i++) {
                        ps.setObject(i, pgVector);
                    }
                    ps.setDouble(11, minScore);
                    ps.setInt(12, candidateTopK);
                },
                this::mapRow
        );

        return rerank(rawItems, queryText).stream()
                .limit(safeTopK)
                .toList();
    }

    private double resolveMinScore(Double minScoreOverride) {
        double fallback = properties.getMinScore();
        if (minScoreOverride == null) {
            return fallback;
        }
        return Math.max(-1.0d, Math.min(1.0d, minScoreOverride));
    }

    private SemanticSearchItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SemanticSearchItem.builder()
                .productId(UUID.fromString(rs.getString("product_id")))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .ingredients(rs.getString("ingredients"))
                .usageInstructions(rs.getString("usage_instructions"))
                .categoryName(rs.getString("category_name"))
                .brandName(rs.getString("brand_name"))
                .suitableSkinTypes(splitList(rs.getString("suitable_skin_types")))
                .skinConcerns(splitList(rs.getString("skin_concerns")))
                .minPrice(rs.getBigDecimal("min_price"))
                .maxPrice(rs.getBigDecimal("max_price"))
                .score(rs.getDouble("score"))
                .build();
    }

    private List<SemanticSearchItem> rerank(List<SemanticSearchItem> items, String queryText) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        String normalizedQuery = normalize(queryText);
        if (normalizedQuery.isBlank()) {
            return items;
        }
        return items.stream()
                .filter(Objects::nonNull)
                .sorted((left, right) -> Double.compare(
                        rerankedScore(right, normalizedQuery),
                        rerankedScore(left, normalizedQuery)
                ))
                .map(item -> item.toBuilder().score(rerankedScore(item, normalizedQuery)).build())
                .toList();
    }

    private double rerankedScore(SemanticSearchItem item, String normalizedQuery) {
        double vectorScore = item.getScore() == null ? 0.0d : item.getScore();
        double lexicalScore = lexicalScore(item, normalizedQuery);
        return (vectorScore * VECTOR_SCORE_WEIGHT) + (lexicalScore * LEXICAL_SCORE_WEIGHT);
    }

    private double lexicalScore(SemanticSearchItem item, String normalizedQuery) {
        Set<String> queryTokens = extractTokens(normalizedQuery);
        if (queryTokens.isEmpty()) {
            return 0.0d;
        }
        double score = 0.0d;
        score += overlapScore(queryTokens, normalize(item.getName())) * 3.2d;
        score += overlapScore(queryTokens, normalize(item.getBrandName())) * 2.3d;
        score += overlapScore(queryTokens, normalize(item.getCategoryName())) * 2.0d;
        score += overlapScore(queryTokens, joinNormalized(item.getSuitableSkinTypes())) * 1.6d;
        score += overlapScore(queryTokens, joinNormalized(item.getSkinConcerns())) * 1.6d;
        score += overlapScore(queryTokens, normalize(item.getDescription())) * 1.2d;
        score += overlapScore(queryTokens, normalize(item.getIngredients())) * 1.0d;
        score += overlapScore(queryTokens, normalize(item.getUsageInstructions())) * 0.8d;

        if (containsPhrase(normalizedQuery, item.getName())) {
            score += 1.4d;
        }
        if (containsPhrase(normalizedQuery, item.getBrandName())) {
            score += 1.2d;
        }
        if (containsPhrase(normalizedQuery, item.getCategoryName())) {
            score += 1.0d;
        }
        return score;
    }

    private double overlapScore(Set<String> queryTokens, String fieldText) {
        Set<String> fieldTokens = extractTokens(fieldText);
        if (fieldTokens.isEmpty()) {
            return 0.0d;
        }
        long matched = queryTokens.stream().filter(fieldTokens::contains).count();
        return (double) matched / (double) queryTokens.size();
    }

    private boolean containsPhrase(String normalizedQuery, String fieldValue) {
        String normalizedField = normalize(fieldValue);
        return !normalizedField.isBlank() && normalizedQuery.contains(normalizedField);
    }

    private String joinNormalized(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .filter(StringUtils::hasText)
                .map(this::normalize)
                .collect(Collectors.joining(" "));
    }

    private Set<String> extractTokens(String text) {
        if (!StringUtils.hasText(text)) {
            return Set.of();
        }
        return Arrays.stream(text.split("[^a-z0-9]+"))
                .map(String::trim)
                .filter(token -> token.length() >= 2)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String weightedScoreSql() {
        return "GREATEST("
                + "1 - (COALESCE(e.identity_embedding, e.embedding) <=> ?::vector), "
                + "1 - (COALESCE(e.benefit_embedding, e.embedding) <=> ?::vector), "
                + "1 - (COALESCE(e.ingredient_embedding, e.embedding) <=> ?::vector), "
                + "1 - (COALESCE(e.usage_embedding, e.embedding) <=> ?::vector), "
                + "1 - (e.embedding <=> ?::vector)"
                + ")";
    }

    private String normalize(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('\u0111', 'd')
                .replace('\u0110', 'D')
                .toLowerCase(Locale.ROOT)
                .trim();
        return normalized.replaceAll("\\s+", " ");
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

    private void validateDimension(List<Double> embedding) {
        int expected = properties.getVectorDimension();
        if (expected > 0 && embedding.size() != expected) {
            throw new IllegalArgumentException(
                    "Unexpected query embedding dimension: expected=" + expected + ", actual=" + embedding.size()
            );
        }
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
