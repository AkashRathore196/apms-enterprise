CREATE TABLE organization (
  organization_id UUID PRIMARY KEY,
  party_id UUID NOT NULL UNIQUE REFERENCES party(party_id),
  legal_name VARCHAR(300) NOT NULL,
  display_name VARCHAR(300)
);
CREATE INDEX idx_organization_legal_name ON organization (lower(legal_name));
