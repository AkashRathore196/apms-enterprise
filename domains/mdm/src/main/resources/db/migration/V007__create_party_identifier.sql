CREATE TABLE party_identifier (
  identifier_id UUID PRIMARY KEY,
  party_id UUID NOT NULL,
  identifier_type VARCHAR(64) NOT NULL,
  identifier_value VARCHAR(512) NOT NULL,
  normalized_identifier_value VARCHAR(512) NOT NULL,
  issuing_authority VARCHAR(128),
  issuing_jurisdiction VARCHAR(128),
  source_system VARCHAR(128),
  valid_from TIMESTAMPTZ,
  valid_to TIMESTAMPTZ,
  is_primary BOOLEAN NOT NULL DEFAULT FALSE,
  lifecycle_state VARCHAR(32) NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_party_identifier_party
    FOREIGN KEY (party_id) REFERENCES party(party_id),
  CONSTRAINT chk_party_identifier_validity
    CHECK (valid_to IS NULL OR valid_from IS NULL OR valid_to >= valid_from),
  CONSTRAINT chk_party_identifier_lifecycle
    CHECK (lifecycle_state IN ('ACTIVE', 'SUSPENDED', 'EXPIRED', 'RETIRED'))
);

CREATE INDEX idx_party_identifier_party
  ON party_identifier (party_id);

CREATE INDEX idx_party_identifier_normalized
  ON party_identifier (identifier_type, normalized_identifier_value);

CREATE UNIQUE INDEX uq_party_identifier_active_identity
  ON party_identifier (
    identifier_type,
    normalized_identifier_value,
    COALESCE(issuing_authority, ''),
    COALESCE(issuing_jurisdiction, '')
  )
  WHERE lifecycle_state IN ('ACTIVE', 'SUSPENDED');

CREATE UNIQUE INDEX uq_party_identifier_primary
  ON party_identifier (party_id, identifier_type)
  WHERE is_primary = TRUE AND lifecycle_state = 'ACTIVE';
