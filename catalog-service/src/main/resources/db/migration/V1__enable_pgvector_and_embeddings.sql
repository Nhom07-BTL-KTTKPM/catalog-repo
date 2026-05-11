CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS product_embeddings (
    product_id UUID PRIMARY KEY REFERENCES products(product_id) ON DELETE CASCADE,
    embedding VECTOR(768),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_product_embeddings_vector
    ON product_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
