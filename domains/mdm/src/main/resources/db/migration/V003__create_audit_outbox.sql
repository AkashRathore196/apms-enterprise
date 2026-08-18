CREATE TABLE audit_entry (
  audit_id UUID PRIMARY KEY,
  entity_type VARCHAR(64) NOT NULL,
  entity_id UUID NOT NULL,
  operation VARCHAR(64) NOT NULL,
  actor VARCHAR(255) NOT NULL,
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox_event (
  event_id UUID PRIMARY KEY,
  aggregate_type VARCHAR(64) NOT NULL,
  aggregate_id UUID NOT NULL,
  event_type VARCHAR(200) NOT NULL,
  event_version INTEGER NOT NULL,
  payload TEXT NOT NULL,
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX idx_outbox_pending ON outbox_event(status, occurred_at);
