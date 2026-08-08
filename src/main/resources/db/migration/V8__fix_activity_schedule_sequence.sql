SELECT setval(
    pg_get_serial_sequence('"ActivitySchedule"', 'id'),
    COALESCE((SELECT MAX("id") FROM "ActivitySchedule"), 0) + 1,
    false
);
