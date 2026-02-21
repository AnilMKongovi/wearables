-- Auth service schema
-- Managed by Flyway; do NOT edit manually after initial deployment.

-- Password-based credentials, one row per user (shared PK with user-service.users)
CREATE TABLE IF NOT EXISTS user_credentials (
    user_id               UUID         NOT NULL,
    password_hash         TEXT         NOT NULL,
    password_algo         VARCHAR(32)  NOT NULL DEFAULT 'bcrypt',
    password_updated_at   TIMESTAMPTZ  NOT NULL,
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    locked_until          TIMESTAMPTZ,

    CONSTRAINT pk_user_credentials PRIMARY KEY (user_id)
);

-- WebAuthn / Passkey credentials (one user may have many devices)
CREATE TABLE IF NOT EXISTS webauthn_credentials (
    id              UUID        NOT NULL,
    user_id         UUID        NOT NULL,
    credential_id   TEXT        NOT NULL,
    public_key_cose BYTEA       NOT NULL,
    signature_count BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT pk_webauthn_credentials  PRIMARY KEY (id),
    CONSTRAINT uq_webauthn_credential_id UNIQUE (credential_id)
);

CREATE INDEX IF NOT EXISTS idx_webauthn_user_id ON webauthn_credentials (user_id);
