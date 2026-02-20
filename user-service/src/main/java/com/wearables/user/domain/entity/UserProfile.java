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
import java.time.LocalDate;
import java.util.UUID;

/**
 * Extended profile information for a user.
 *
 * <p>The primary key mirrors the user ID (shared PK / one-to-one) so no
 * separate surrogate key is required.  All fields are optional — the profile
 * row is created automatically when a user is registered.
 */
@Entity
@Table(name = "user_profiles")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserProfile {

    /** Shared primary key with the {@code users} table. */
    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "userId")
    private User user;

    private String firstName;

    private String lastName;

    /** Preferred display name, falls back to firstName + lastName. */
    private String displayName;

    private LocalDate dateOfBirth;

    /** Free-form gender identity string. */
    private String gender;

    /** URL to the user's avatar image. */
    private String avatarUrl;

    /** Short bio / about text. */
    @Column(length = 500)
    private String bio;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
