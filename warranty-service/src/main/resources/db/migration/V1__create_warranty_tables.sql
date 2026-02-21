-- Warranty service schema
-- Managed by Flyway; do NOT edit manually after initial deployment.

-- ============================================================================
-- warranty_registrations
-- ============================================================================
CREATE TABLE IF NOT EXISTS warranty_registrations (
    id                      UUID            NOT NULL,
    user_id                 UUID            NOT NULL,
    device_registry_id      UUID,                               -- nullable; set when device is activated
    serial_number           TEXT            NOT NULL,
    product_id              VARCHAR(128)    NOT NULL,
    product_sku             VARCHAR(128)    NOT NULL,
    product_name            VARCHAR(256)    NOT NULL,
    purchase_date           DATE            NOT NULL,
    warranty_start_date     DATE            NOT NULL,
    warranty_end_date       DATE            NOT NULL,
    warranty_duration_months INT            NOT NULL,
    warranty_type           VARCHAR(32)     NOT NULL,
    status                  VARCHAR(32)     NOT NULL DEFAULT 'PENDING_VERIFICATION',
    verification_status     VARCHAR(32)     NOT NULL DEFAULT 'UNVERIFIED',
    seller_type             VARCHAR(32)     NOT NULL,
    seller_name             VARCHAR(256),
    order_number            VARCHAR(128)    NOT NULL,
    invoice_url             TEXT,
    purchase_price          NUMERIC(12, 2),
    currency                CHAR(3),
    purchase_country        CHAR(2)         NOT NULL,
    registered_at           TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_warranty_registrations PRIMARY KEY (id),
    CONSTRAINT uq_warranty_serial_number UNIQUE (serial_number)
);

CREATE INDEX IF NOT EXISTS idx_warranty_user_id      ON warranty_registrations (user_id);
CREATE INDEX IF NOT EXISTS idx_warranty_status       ON warranty_registrations (status);
CREATE INDEX IF NOT EXISTS idx_warranty_end_date     ON warranty_registrations (warranty_end_date);

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

    CONSTRAINT pk_warranty_claims      PRIMARY KEY (id),
    CONSTRAINT fk_claim_warranty       FOREIGN KEY (warranty_id)
                                           REFERENCES warranty_registrations (id)
                                           ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_claim_warranty_id ON warranty_claims (warranty_id);
CREATE INDEX IF NOT EXISTS idx_claim_status      ON warranty_claims (status);
