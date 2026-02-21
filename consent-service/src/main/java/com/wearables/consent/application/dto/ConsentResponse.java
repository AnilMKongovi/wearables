/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.application.dto;

import com.wearables.consent.domain.entity.ConsentType;
import com.wearables.consent.domain.entity.UserConsent;

import java.time.Instant;
import java.util.UUID;

public record ConsentResponse(
        UUID id,
        UUID userId,
        ConsentType consentType,
        boolean granted,
        int version,
        Instant grantedAt,
        Instant revokedAt,
        Instant updatedAt
) {
    public static ConsentResponse from(UserConsent c) {
        return new ConsentResponse(
                c.getId(), c.getUserId(), c.getConsentType(),
                c.isGranted(), c.getVersion(),
                c.getGrantedAt(), c.getRevokedAt(), c.getUpdatedAt()
        );
    }
}
