package iuh.fit.catalogservice.service;

public class EmbeddingReindexLockedException extends RuntimeException {

    public EmbeddingReindexLockedException() {
        super("Embedding reindex is already running");
    }
}
