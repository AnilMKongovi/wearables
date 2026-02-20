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
 * Per-user notification and display preferences.
 *
 * <p>Shares primary key with the {@code users} table (one-to-one). Defaults
 * are set at creation time and may be updated by the user at any time.
 */
@Entity
@Table(name = "user_preferences")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserPreferences {

    /** Shared primary key with the {@code users} table. */
    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "userId")
    private User user;

    /** BCP-47 language tag, e.g. "en", "hi", "ta". Defaults to "en". */
    @Column(nullable = false)
    @Builder.Default
    private String language = "en";

    /** IANA timezone ID, e.g. "Asia/Kolkata". Defaults to "UTC". */
    @Column(nullable = false)
    @Builder.Default
    private String timezone = "UTC";

    /** Master switch — disabling this suppresses all notification channels. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean notificationsEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailNotificationsEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean smsNotificationsEnabled = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pushNotificationsEnabled = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
