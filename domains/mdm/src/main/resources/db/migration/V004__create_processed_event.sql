CREATE TABLE processed_event (
  event_id VARCHAR(100) PRIMARY KEY,
  consumer_group VARCHAR(200) NOT NULL,
  processed_at TIMESTAMPTZ NOT NULL
);
