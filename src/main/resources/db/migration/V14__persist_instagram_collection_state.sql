CREATE TABLE instagram_collection_state (
    id INTEGER PRIMARY KEY CHECK (id = 1),
    run_id VARCHAR(255),
    item_offset INTEGER NOT NULL DEFAULT 0,
    failures INTEGER NOT NULL DEFAULT 0,
    started_at TIMESTAMP WITH TIME ZONE,
    checkpoint TIMESTAMP WITH TIME ZONE,
    had_post_failure BOOLEAN NOT NULL DEFAULT FALSE,
    lease_owner VARCHAR(255),
    lease_until TIMESTAMP WITH TIME ZONE
);
INSERT INTO instagram_collection_state (id) VALUES (1);
