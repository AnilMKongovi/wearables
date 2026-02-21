/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores a user's consent decision for a single {@link ConsentType}.
 *
 * <p>One row per (userId, consentType). Each grant or revocation increments
 * {@code version} and records an IP address for regulatory audit purposes.
 */
@Entity
@Table(name = "user_consents", uniqueConstraints = {
        @UniqueConstraint(name = "uq_consent_user_type",
                columnNames = {"user_id", "consent_type"})
}, indexes = {
        @Index(name = "idx_consent_user_id", columnList = "user_id")
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserConsent {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 40)
    private ConsentType consentType;

    /** {@code true} = GRANTED, {@code false} = REVOKED. */
    @Column(name = "granted", nullable = false)
    private boolean granted;

    /**
     * Monotonically increasing version number, incremented on every
     * grant/revoke action. Useful for optimistic-locking and audit replay.
     */
    @Column(name = "version", nullable = false)
    private int version;

    /** Timestamp of the most recent GRANT action (null if never granted). */
    @Column(name = "granted_at")
    private Instant grantedAt;

    /** Timestamp of the most recent REVOKE action (null if never revoked). */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Client IP address recorded at the time of the last consent action.
     * Required for India DPDP Act compliance audit trails.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
