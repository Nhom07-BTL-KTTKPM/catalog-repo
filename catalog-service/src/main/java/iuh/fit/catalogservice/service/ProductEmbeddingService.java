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

        ProductEmbeddingTexts texts = buildEmbeddingTexts(product);
        if (!StringUtils.hasText(texts.fullText())) {
            return;
        }

        ProductEmbeddingStore.EmbeddingVectors vectors = new ProductEmbeddingStore.EmbeddingVectors(
                embeddingClient.embed(texts.fullText()),
                embedOptional(texts.identityText()),
                embedOptional(texts.benefitText()),
                embedOptional(texts.ingredientText()),
                embedOptional(texts.usageText())
        );
        embeddingStore.upsert(product.getProductId(), vectors);
    }

    private List<Double> embedOptional(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        return embeddingClient.embed(text);
    }

    private ProductEmbeddingTexts buildEmbeddingTexts(Product product) {
        List<String> identityChunks = new ArrayList<>();
        addLabeled(identityChunks, "document_type", "catalog_product");
        addLabeled(identityChunks, "name", product.getName());
        if (product.getBrand() != null) {
            addLabeled(identityChunks, "brand.name", product.getBrand().getName());
        }
        if (product.getCategory() != null) {
            addLabeled(identityChunks, "category.name", product.getCategory().getName());
        }

        List<String> benefitChunks = new ArrayList<>();
        if (product.getSuitableSkinTypes() != null) {
            addJoined(benefitChunks, "suitableSkinTypes", product.getSuitableSkinTypes());
        }
        if (product.getSkinConcerns() != null) {
            addJoined(benefitChunks, "skinConcerns", product.getSkinConcerns());
        }
        addLabeled(benefitChunks, "description", product.getDescription());

        List<String> ingredientChunks = new ArrayList<>();
        addLabeled(ingredientChunks, "ingredients", product.getIngredients());

        List<String> usageChunks = new ArrayList<>();
        addLabeled(usageChunks, "usageInstructions", product.getUsageInstructions());

        List<String> fullChunks = new ArrayList<>();
        fullChunks.addAll(identityChunks);
        fullChunks.addAll(benefitChunks);
        fullChunks.addAll(ingredientChunks);
        fullChunks.addAll(usageChunks);
        addSearchDocument(fullChunks, product);

        return new ProductEmbeddingTexts(
                String.join("\n", fullChunks),
                String.join("\n", identityChunks),
                String.join("\n", benefitChunks),
                String.join("\n", ingredientChunks),
                String.join("\n", usageChunks)
        );
    }

    private void addSearchDocument(List<String> chunks, Product product) {
        List<String> compact = new ArrayList<>();
        addIfPresent(compact, product.getName());
        addIfPresent(compact, product.getName());
        if (product.getBrand() != null) {
            addIfPresent(compact, product.getBrand().getName());
            addIfPresent(compact, product.getBrand().getName());
        }
        if (product.getCategory() != null) {
            addIfPresent(compact, product.getCategory().getName());
            addIfPresent(compact, product.getCategory().getName());
        }
        if (product.getSuitableSkinTypes() != null) {
            compact.addAll(normalizeValues(product.getSuitableSkinTypes()));
        }
        if (product.getSkinConcerns() != null) {
            compact.addAll(normalizeValues(product.getSkinConcerns()));
        }
        addIfPresent(compact, product.getDescription());
        addIfPresent(compact, product.getIngredients());
        addIfPresent(compact, product.getUsageInstructions());
        if (!compact.isEmpty()) {
            chunks.add("search_document: " + String.join(" | ", compact));
        }
    }

    private void addIfPresent(List<String> chunks, String value) {
        if (StringUtils.hasText(value)) {
            chunks.add(value.trim());
        }
    }

    private void addLabeled(List<String> chunks, String label, String value) {
        if (StringUtils.hasText(value)) {
            chunks.add(label + ": " + value.trim());
        }
    }

    private void addJoined(List<String> chunks, String label, List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        List<String> normalized = normalizeValues(values);
        if (!normalized.isEmpty()) {
            chunks.add(label + ": " + String.join(", ", normalized));
        }
    }

    private List<String> normalizeValues(List<String> values) {
        return values.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .toList();
    }

    public record ReindexResult(int processed, int failed, Instant startedAt, Instant finishedAt) {
    }

    private record ProductEmbeddingTexts(
            String fullText,
            String identityText,
            String benefitText,
            String ingredientText,
            String usageText
    ) {
    }
}
