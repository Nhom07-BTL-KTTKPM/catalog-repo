UPDATE product_embeddings
SET embedding = NULL;

ALTER TABLE product_embeddings
    ALTER COLUMN embedding TYPE VECTOR(768);
