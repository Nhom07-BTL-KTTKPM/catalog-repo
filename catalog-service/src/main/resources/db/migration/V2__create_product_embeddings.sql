CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS product_embeddings (
    product_id UUID PRIMARY KEY REFERENCES products(product_id) ON DELETE CASCADE,
    embedding VECTOR(3072),
    updated_at TIMESTAMP DEFAULT NOW()
);
