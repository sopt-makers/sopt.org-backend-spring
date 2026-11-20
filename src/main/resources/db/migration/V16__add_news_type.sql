ALTER TABLE main_news ADD COLUMN type VARCHAR(20);

UPDATE main_news
SET type = CASE
    WHEN instagram_post_id IS NOT NULL
        OR lower(link) ~ '^https?://([a-z0-9-]+[.])*instagram[.]com([/:?#]|$)' THEN 'INSTAGRAM'
    WHEN lower(link) ~ '^https?://([a-z0-9-]+[.])*(blog[.]naver[.]com|tistory[.]com|velog[.]io|brunch[.]co[.]kr|medium[.]com|blog[.]sopt[.]org)([/:?#]|$)' THEN 'BLOG'
    ELSE 'OTHER'
END;

ALTER TABLE main_news ALTER COLUMN type SET NOT NULL;
ALTER TABLE main_news ADD CONSTRAINT main_news_type_check CHECK (type IN ('BLOG', 'INSTAGRAM', 'OTHER'));
