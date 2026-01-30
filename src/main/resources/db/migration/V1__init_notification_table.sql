-- V1: Notification 테이블 초기 생성
-- unique constraint 포함된 초기 상태

CREATE TABLE "Notification" (
                                "id" BIGSERIAL PRIMARY KEY,
                                "email" VARCHAR(255) NOT NULL,
                                "generation" INTEGER NOT NULL,
                                "createdAt" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT "uk_notification_email_generation" UNIQUE ("email", "generation")
);

-- 인덱스 추가 (조회 성능 향상)
CREATE INDEX "idx_notification_email" ON "Notification"("email");
CREATE INDEX "idx_notification_generation" ON "Notification"("generation");

-- 코멘트 추가
COMMENT ON TABLE "Notification" IS '모집 알림 신청';
COMMENT ON COLUMN "Notification"."id" IS 'PK';
COMMENT ON COLUMN "Notification"."email" IS '알림받을 이메일';
COMMENT ON COLUMN "Notification"."generation" IS '알림 신청한 기수';
COMMENT ON COLUMN "Notification"."createdAt" IS '신청 일시';
