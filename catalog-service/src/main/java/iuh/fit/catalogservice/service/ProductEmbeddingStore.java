package iuh.fit.catalogservice.service;

import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductEmbeddingStore {

    private final JdbcTemplate jdbcTemplate;

    public ProductEmbeddingStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsert(UUID productId, EmbeddingVectors vectors) {
        if (vectors == null || vectors.embedding() == null || vectors.embedding().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO product_embeddings ("
                + "product_id, embedding, identity_embedding, benefit_embedding, ingredient_embedding, usage_embedding, updated_at"
                + ") VALUES (?, ?::vector, ?::vector, ?::vector, ?::vector, ?::vector, NOW()) "
                + "ON CONFLICT (product_id) "
                + "DO UPDATE SET "
                + "embedding = EXCLUDED.embedding, "
                + "identity_embedding = EXCLUDED.identity_embedding, "
                + "benefit_embedding = EXCLUDED.benefit_embedding, "
                + "ingredient_embedding = EXCLUDED.ingredient_embedding, "
                + "usage_embedding = EXCLUDED.usage_embedding, "
                + "updated_at = NOW()";

        jdbcTemplate.update(
                sql,
                productId,
                toPgVector(vectors.embedding()),
                toPgVector(vectors.identityEmbedding()),
                toPgVector(vectors.benefitEmbedding()),
                toPgVector(vectors.ingredientEmbedding()),
                toPgVector(vectors.usageEmbedding())
        );
    }

    public void delete(UUID productId) {
        jdbcTemplate.update("DELETE FROM product_embeddings WHERE product_id = ?", productId);
    }

    private PGobject toPgVector(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return null;
        }
        String vectorLiteral = embedding.stream()
                .map(value -> String.format(Locale.ROOT, "%s", value))
                .collect(Collectors.joining(",", "[", "]"));

        PGobject pgVector = new PGobject();
        pgVector.setType("vector");
        try {
            pgVector.setValue(vectorLiteral);
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Invalid embedding vector format", ex);
        }
        return pgVector;
    }

    public record EmbeddingVectors(
            List<Double> embedding,
            List<Double> identityEmbedding,
            List<Double> benefitEmbedding,
            List<Double> ingredientEmbedding,
            List<Double> usageEmbedding
    ) {
    }
}
