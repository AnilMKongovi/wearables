-- Claim service schema
-- Managed by Flyway; do NOT edit manually after initial deployment.

-- ============================================================================
-- warranty_claims
-- ============================================================================
CREATE TABLE IF NOT EXISTS warranty_claims (
    id                  UUID            NOT NULL,
    warranty_id         UUID            NOT NULL,
    user_id             UUID            NOT NULL,
    claim_type          VARCHAR(32)     NOT NULL,
    status              VARCHAR(32)     NOT NULL DEFAULT 'OPEN',
    description         VARCHAR(2000)   NOT NULL,
    resolution_notes    VARCHAR(2000),
    opened_at           TIMESTAMPTZ     NOT NULL,
    updated_at          TIMESTAMPTZ     NOT NULL,
    resolved_at         TIMESTAMPTZ,

    CONSTRAINT pk_warranty_claims PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_claim_warranty_id ON warranty_claims (warranty_id);
CREATE INDEX IF NOT EXISTS idx_claim_status      ON warranty_claims (status);
