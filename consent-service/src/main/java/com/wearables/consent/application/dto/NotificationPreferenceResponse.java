/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.application.dto;

import com.wearables.consent.domain.entity.NotificationPreference;

import java.time.Instant;
import java.util.UUID;

public record NotificationPreferenceResponse(
        UUID id,
        UUID userId,
        boolean pushEnabled,
        boolean quietHoursEnabled,
        String quietHoursStart,
        String quietHoursEnd,
        boolean dndEnabled,
        boolean alertsEnabled,
        boolean remindersEnabled,
        boolean marketingEnabled,
        Instant updatedAt
) {
    public static NotificationPreferenceResponse from(NotificationPreference p) {
        return new NotificationPreferenceResponse(
                p.getId(), p.getUserId(),
                p.isPushEnabled(), p.isQuietHoursEnabled(),
                p.getQuietHoursStart(), p.getQuietHoursEnd(),
                p.isDndEnabled(), p.isAlertsEnabled(),
                p.isRemindersEnabled(), p.isMarketingEnabled(),
                p.getUpdatedAt()
        );
    }

    /** Sensible defaults returned when a user has no stored preference yet. */
    public static NotificationPreferenceResponse defaults(UUID userId) {
        return new NotificationPreferenceResponse(
                null, userId,
                true,  // pushEnabled
                false, // quietHoursEnabled
                null, null,
                false, // dndEnabled
                true,  // alertsEnabled
                true,  // remindersEnabled
                false, // marketingEnabled
                null
        );
    }
}
