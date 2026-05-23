CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS product_embeddings (
    product_id UUID PRIMARY KEY,
    embedding VECTOR(3072),
    updated_at TIMESTAMP DEFAULT NOW()
);
