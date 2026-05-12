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

    public void upsert(UUID productId, List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return;
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

        String sql = "INSERT INTO product_embeddings (product_id, embedding, updated_at) "
                + "VALUES (?, ?::vector, NOW()) "
                + "ON CONFLICT (product_id) "
                + "DO UPDATE SET embedding = EXCLUDED.embedding, updated_at = NOW()";

        jdbcTemplate.update(sql, productId, pgVector);
    }

    public void delete(UUID productId) {
        jdbcTemplate.update("DELETE FROM product_embeddings WHERE product_id = ?", productId);
    }
}
