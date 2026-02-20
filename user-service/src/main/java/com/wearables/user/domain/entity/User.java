/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.

 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
*/


package com.pranaah.wearables.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    private UUID id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String countryCode;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean emailVerified;
    private Boolean phoneVerified;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    public enum Status {
        ACTIVE, INACTIVE, LOCKED, DELETED
    }
}

