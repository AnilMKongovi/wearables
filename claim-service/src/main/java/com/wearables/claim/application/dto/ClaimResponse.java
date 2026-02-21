/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.application.dto;

import com.wearables.claim.domain.entity.ClaimStatus;
import com.wearables.claim.domain.entity.ClaimType;
import com.wearables.claim.domain.entity.WarrantyClaim;

import java.time.Instant;
import java.util.UUID;

public record ClaimResponse(
        UUID id,
        UUID warrantyId,
        UUID userId,
        ClaimType claimType,
        ClaimStatus status,
        String description,
        String resolutionNotes,
        Instant openedAt,
        Instant updatedAt,
        Instant resolvedAt
) {
    public static ClaimResponse from(WarrantyClaim c) {
        return new ClaimResponse(
                c.getId(), c.getWarrantyId(), c.getUserId(),
                c.getClaimType(), c.getStatus(),
                c.getDescription(), c.getResolutionNotes(),
                c.getOpenedAt(), c.getUpdatedAt(), c.getResolvedAt()
        );
    }
}
