ALTER TABLE main_news ALTER COLUMN title TYPE TEXT;
ALTER TABLE main_news ADD COLUMN IF NOT EXISTS instagram_post_id VARCHAR(255) UNIQUE;
ALTER TABLE main_news ADD COLUMN IF NOT EXISTS published_at TIMESTAMP WITH TIME ZONE;
CREATE INDEX IF NOT EXISTS idx_main_news_published_at ON main_news (published_at DESC);
