-- 기존 수동 뉴스는 저장된 등록 시각을 게시 시각으로 사용한다.
UPDATE main_news
SET published_at = created_at AT TIME ZONE 'UTC'
WHERE published_at IS NULL;
