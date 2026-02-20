/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a registered device owned by a user.
 *
 * <p>A device maps a user to a push subscription (OneSignal player ID / FCM token)
 * and carries metadata about the physical form factor, OS version, and lifecycle.
 * One user may have multiple active devices (phone + ring + glasses, etc.).
 */
@Entity
@Table(
    name = "devices",
    indexes = {
        @Index(name = "idx_devices_user_id", columnList = "userId"),
        @Index(name = "idx_devices_subscription_id", columnList = "subscriptionId")
    }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Device {

    /** Surrogate primary key. */
    @Id
    private UUID id;

    /** Foreign key to the users table (owned by user-service). */
    @Column(nullable = false)
    private UUID userId;

    /**
     * Hardware/OS device identifier (e.g. Android ANDROID_ID, iOS identifierForVendor,
     * or a client-generated UUID for web/wearables). Used to detect re-registration
     * of the same physical device.
     */
    @Column(nullable = false)
    private String deviceId;

    /**
     * Push subscription token, e.g. OneSignal player_id or raw FCM/APNs token.
     * Nullable because wearables (SMART_RING, SMART_GLASSES) inherit push via
     * their paired mobile device and may not hold an independent token.
     */
    @Column
    private String subscriptionId;

    /** Physical form factor of the device. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    /** Lifecycle status — inactive devices are excluded from push targeting. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    /** Semantic version of the Pranaah app installed on this device. */
    private String appVersion;

    /** OS version string (e.g. "Android 14", "iOS 17.4", "FirmwareOS 2.1"). */
    private String osVersion;

    /** Timestamp when the device was first registered. */
    @Column(nullable = false, updatable = false)
    private Instant registeredAt;

    /** Timestamp of the last update (token refresh, OS upgrade, etc.). */
    @Column(nullable = false)
    private Instant updatedAt;

    /** Timestamp of the last time this device was seen active (heartbeat). */
    private Instant lastSeenAt;
}
