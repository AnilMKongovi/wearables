/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.

 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
*/


package com.pranaah.wearables.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_credentials")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserCredential {

    @Id
    private UUID userId;

    private String passwordHash;
    private String passwordAlgo;
    private Instant passwordUpdatedAt;
    private int failedLoginAttempts;
    private Instant lockedUntil;
}
