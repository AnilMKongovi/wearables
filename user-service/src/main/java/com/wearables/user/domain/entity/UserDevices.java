/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight device reference owned by the user-service.
 *
 * <p>Full push-subscription details (OneSignal player ID, token rotation, etc.)
 * are managed by <em>device-registry-service</em>.  This table stores only the
 * minimal identifiers needed to answer "which devices belong to this user"
 * without an inter-service call.
 *
 * <p>Rows are inserted / deactivated via Kafka events emitted by
 * device-registry-service whenever a device is registered or deactivated.
 */
@Entity
@Table(
    name = "user_devices",
    indexes = {
        @Index(name = "idx_user_devices_user_id", columnList = "userId")
    }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserDevices {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    /**
     * Hardware/OS device identifier — matches {@code deviceId} in
     * device-registry-service.
     */
    @Column(nullable = false)
    private String deviceId;

    /** Platform string matching {@code DeviceType} in device-registry-service. */
    @Column(nullable = false)
    private String platform;

    @Column(nullable = false, updatable = false)
    private Instant registeredAt;

    /** Set when the device is deactivated; null means still active. */
    private Instant deactivatedAt;
}
