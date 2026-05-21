package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.config.EmbeddingProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingReindexLockService {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingProperties properties;

    public EmbeddingReindexLockService(JdbcTemplate jdbcTemplate, EmbeddingProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    public boolean tryLock() {
        if (!properties.getReindex().getLock().isEnabled()) {
            return true;
        }

        Long key = properties.getReindex().getLock().getKey();
        Boolean locked = jdbcTemplate.queryForObject(
                "select pg_try_advisory_lock(?)",
                Boolean.class,
                key
        );
        return Boolean.TRUE.equals(locked);
    }

    public void unlock() {
        if (!properties.getReindex().getLock().isEnabled()) {
            return;
        }

        Long key = properties.getReindex().getLock().getKey();
        jdbcTemplate.queryForObject(
                "select pg_advisory_unlock(?)",
                Boolean.class,
                key
        );
    }
}
