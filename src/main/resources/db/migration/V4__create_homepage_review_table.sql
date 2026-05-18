CREATE TABLE "HomepageReview" (
    "id"         BIGSERIAL    NOT NULL,
    "title"      VARCHAR(255) NOT NULL,
    "content"    VARCHAR(200) NOT NULL,
    "authorInfo" VARCHAR(255) NOT NULL,
    "createdAt"  TIMESTAMP    NOT NULL DEFAULT NOW(),
    "updatedAt"  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT "HomepageReview_pkey" PRIMARY KEY ("id")
);
