ALTER TABLE product_embeddings
    ADD COLUMN IF NOT EXISTS identity_embedding VECTOR(768),
    ADD COLUMN IF NOT EXISTS benefit_embedding VECTOR(768),
    ADD COLUMN IF NOT EXISTS ingredient_embedding VECTOR(768),
    ADD COLUMN IF NOT EXISTS usage_embedding VECTOR(768);

