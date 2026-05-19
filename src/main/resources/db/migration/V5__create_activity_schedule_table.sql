CREATE TABLE "ActivitySchedule" (
    "id"           BIGSERIAL    NOT NULL,
    "generationId" INTEGER      NOT NULL,
    "name"         VARCHAR(100) NOT NULL,
    "startDate"    DATE         NOT NULL,
    "endDate"      DATE,
    "displayOrder" INTEGER      NOT NULL DEFAULT 0,
    "createdAt"    TIMESTAMP    NOT NULL DEFAULT NOW(),
    "updatedAt"    TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT "ActivitySchedule_pkey" PRIMARY KEY ("id")
);
