ALTER TABLE main_news ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;
DROP INDEX IF EXISTS idx_main_news_published_at;
CREATE INDEX idx_main_news_visible_published_at ON main_news (published_at DESC NULLS LAST, created_at DESC, id DESC) WHERE hidden = FALSE;
