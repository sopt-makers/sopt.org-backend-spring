ALTER TABLE "Generation"
    DROP COLUMN IF EXISTS "brandingColorMain",
    DROP COLUMN IF EXISTS "brandingColorSub",
    DROP COLUMN IF EXISTS "brandingColorPoint",
    DROP COLUMN IF EXISTS "brandingColorBackground";

ALTER TABLE "Generation"
    ADD COLUMN IF NOT EXISTS "darkModeKeyColor" VARCHAR(7),
    ADD COLUMN IF NOT EXISTS "darkModeTextColor" VARCHAR(5),
    ADD COLUMN IF NOT EXISTS "lightModeKeyColor" VARCHAR(7),
    ADD COLUMN IF NOT EXISTS "lightModeTextColor" VARCHAR(5);
