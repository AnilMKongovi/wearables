/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "warranty_claims", indexes = {
        @Index(name = "idx_claim_warranty_id", columnList = "warranty_id")
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class WarrantyClaim {

    @Id
    private UUID id;

    /** The warranty this claim is raised against. */
    @Column(name = "warranty_id", nullable = false)
    private UUID warrantyId;

    /** User who raised the claim. */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false, length = 32)
    private ClaimType claimType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ClaimStatus status;

    /** User-provided description of the issue. */
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    /** Agent/admin notes added when the claim is resolved or rejected. */
    @Column(name = "resolution_notes", length = 2000)
    private String resolutionNotes;

    @Column(name = "opened_at", nullable = false, updatable = false)
    private Instant openedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Set when status transitions to RESOLVED or CLOSED. */
    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
