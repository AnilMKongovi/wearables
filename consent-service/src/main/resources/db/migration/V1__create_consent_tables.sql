-- Consent service schema
-- Managed by Flyway; do NOT edit manually after initial deployment.

-- ============================================================================
-- user_consents
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_consents (
    id              UUID            NOT NULL,
    user_id         UUID            NOT NULL,
    consent_type    VARCHAR(40)     NOT NULL,
    granted         BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INT             NOT NULL DEFAULT 0,
    granted_at      TIMESTAMPTZ,
    revoked_at      TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ     NOT NULL,
    ip_address      VARCHAR(45),

    CONSTRAINT pk_user_consents PRIMARY KEY (id),
    CONSTRAINT uq_consent_user_type UNIQUE (user_id, consent_type)
);

CREATE INDEX IF NOT EXISTS idx_consent_user_id ON user_consents (user_id);

-- ============================================================================
-- notification_preferences
-- ============================================================================
CREATE TABLE IF NOT EXISTS notification_preferences (
    id                  UUID        NOT NULL,
    user_id             UUID        NOT NULL,
    push_enabled        BOOLEAN     NOT NULL DEFAULT TRUE,
    quiet_hours_enabled BOOLEAN     NOT NULL DEFAULT FALSE,
    quiet_hours_start   VARCHAR(5),
    quiet_hours_end     VARCHAR(5),
    dnd_enabled         BOOLEAN     NOT NULL DEFAULT FALSE,
    alerts_enabled      BOOLEAN     NOT NULL DEFAULT TRUE,
    reminders_enabled   BOOLEAN     NOT NULL DEFAULT TRUE,
    marketing_enabled   BOOLEAN     NOT NULL DEFAULT FALSE,
    updated_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_notification_preferences PRIMARY KEY (id),
    CONSTRAINT uq_notif_pref_user_id UNIQUE (user_id)
);
