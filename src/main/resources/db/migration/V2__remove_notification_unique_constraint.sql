-- V2: Notification 테이블의 unique constraint 제거
-- 같은 이메일이 같은 기수에 대해 중복 신청 가능하도록 변경

ALTER TABLE "Notification"
DROP CONSTRAINT IF EXISTS "uk_notification_email_generation";

-- 제거 사유 로깅
COMMENT ON TABLE "Notification" IS '모집 알림 신청 (중복 신청 허용)';
