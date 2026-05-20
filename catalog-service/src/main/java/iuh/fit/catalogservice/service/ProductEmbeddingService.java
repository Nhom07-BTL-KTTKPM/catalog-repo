package iuh.fit.catalogservice.service;

import iuh.fit.catalogservice.config.EmbeddingProperties;
import iuh.fit.catalogservice.entity.Product;
import iuh.fit.catalogservice.repo.ProductRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ProductEmbeddingService {

    private final ProductRepository productRepository;
    private final EmbeddingClient embeddingClient;
    private final ProductEmbeddingStore embeddingStore;
    private final EmbeddingProperties properties;
    private final EmbeddingReindexLockService lockService;

    public ProductEmbeddingService(
            ProductRepository productRepository,
            EmbeddingClient embeddingClient,
            ProductEmbeddingStore embeddingStore,
            EmbeddingProperties properties,
            EmbeddingReindexLockService lockService
    ) {
        this.productRepository = productRepository;
        this.embeddingClient = embeddingClient;
        this.embeddingStore = embeddingStore;
        this.properties = properties;
        this.lockService = lockService;
    }

    @Transactional
    public void indexProduct(Product product) {
        if (!properties.isEnabled()) {
            return;
        }
        if (product == null) {
            return;
        }
        indexProductInternal(product);
    }

    @Scheduled(cron = "${embedding.reindex.cron:0 0 * * * *}")
    @Transactional
    public void scheduledReindex() {
        if (!properties.getReindex().isEnabled()) {
            return;
        }
        try {
            reindexAllProducts();
        } catch (EmbeddingReindexLockedException ex) {
            log.info("Embedding reindex skipped because a lock is held");
        }
    }

    @Transactional
    public ReindexResult reindexAllProducts() {
        if (!properties.isEnabled()) {
            return new ReindexResult(0, 0, Instant.now(), Instant.now());
        }

        if (!lockService.tryLock()) {
            throw new EmbeddingReindexLockedException();
        }

        try {
            return doReindex();
        } finally {
            lockService.unlock();
        }
    }

    private ReindexResult doReindex() {
        int batchSize = properties.getReindex().getBatchSize();
        int pageNumber = 0;
        int processed = 0;
        int failed = 0;
        Instant startedAt = Instant.now();

        Page<Product> page;
        do {
            Pageable pageable = PageRequest.of(pageNumber, batchSize);
            page = productRepository.findAllWithBrandAndCategory(pageable);
            for (Product product : page.getContent()) {
                try {
                    indexProductInternal(product);
                    processed++;
                } catch (Exception ex) {
                    failed++;
                    log.warn("Embedding failed for product {}", product.getProductId(), ex);
                }
            }
            pageNumber++;
        } while (page.hasNext());

        Instant finishedAt = Instant.now();
        log.info("Embedding reindex completed: processed={}, failed={}", processed, failed);
        return new ReindexResult(processed, failed, startedAt, finishedAt);
    }

    private void indexProductInternal(Product product) {
        if (Boolean.FALSE.equals(product.getIsActive())) {
            embeddingStore.delete(product.getProductId());
            return;
        }

        String text = buildEmbeddingText(product);
        if (!StringUtils.hasText(text)) {
            return;
        }

        List<Double> embedding = embeddingClient.embed(text);
        embeddingStore.upsert(product.getProductId(), embedding);
    }

    private String buildEmbeddingText(Product product) {
        List<String> chunks = new ArrayList<>();
        addIfPresent(chunks, product.getName());
        addIfPresent(chunks, product.getDescription());
        addIfPresent(chunks, product.getIngredients());
        addIfPresent(chunks, product.getUsageInstructions());
        if (product.getBrand() != null) {
            addIfPresent(chunks, product.getBrand().getName());
        }
        if (product.getCategory() != null) {
            addIfPresent(chunks, product.getCategory().getName());
        }
        if (product.getSuitableSkinTypes() != null) {
            chunks.addAll(product.getSuitableSkinTypes());
        }
        if (product.getSkinConcerns() != null) {
            chunks.addAll(product.getSkinConcerns());
        }
        return String.join("\n", chunks);
    }

    private void addIfPresent(List<String> chunks, String value) {
        if (StringUtils.hasText(value)) {
            chunks.add(value.trim());
        }
    }

    public record ReindexResult(int processed, int failed, Instant startedAt, Instant finishedAt) {
    }
}
