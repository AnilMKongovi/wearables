/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.

 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
*/

// WebAuthn Credential (Passkey)

package com.wearables.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "webauthn_credentials")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WebAuthnCredential {

    @Id
    private UUID id;

    private UUID userId;
    private String credentialId;

    @Lob
    private byte[] publicKeyCose;

    private long signatureCount;
}


