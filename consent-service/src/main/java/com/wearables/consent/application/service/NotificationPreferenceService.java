/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.application.service;

import com.wearables.consent.application.dto.NotificationPreferenceResponse;
import com.wearables.consent.application.dto.UpdateNotificationPreferenceRequest;
import com.wearables.consent.domain.entity.NotificationPreference;
import com.wearables.consent.domain.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    // -------------------------------------------------------------------------
    // Query
    // -------------------------------------------------------------------------

    /**
     * Returns stored preferences, or in-memory defaults if the user has not yet
     * saved any preferences. No row is persisted until the first PUT.
     */
    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreferences(UUID userId) {
        return preferenceRepository.findByUserId(userId)
                .map(NotificationPreferenceResponse::from)
                .orElseGet(() -> NotificationPreferenceResponse.defaults(userId));
    }

    // -------------------------------------------------------------------------
    // Upsert
    // -------------------------------------------------------------------------

    @Transactional
    public NotificationPreferenceResponse updatePreferences(UUID userId,
                                                            UpdateNotificationPreferenceRequest req) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> NotificationPreference.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .build());

        pref.setPushEnabled(req.pushEnabled());
        pref.setQuietHoursEnabled(req.quietHoursEnabled());
        pref.setQuietHoursStart(req.quietHoursStart());
        pref.setQuietHoursEnd(req.quietHoursEnd());
        pref.setDndEnabled(req.dndEnabled());
        pref.setAlertsEnabled(req.alertsEnabled());
        pref.setRemindersEnabled(req.remindersEnabled());
        pref.setMarketingEnabled(req.marketingEnabled());
        pref.setUpdatedAt(Instant.now());

        pref = preferenceRepository.save(pref);
        log.info("Notification preferences updated [userId={}, push={}, dnd={}]",
                userId, req.pushEnabled(), req.dndEnabled());

        return NotificationPreferenceResponse.from(pref);
    }
}
