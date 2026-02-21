/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.api.controller;

import com.wearables.consent.application.dto.NotificationPreferenceResponse;
import com.wearables.consent.application.dto.UpdateNotificationPreferenceRequest;
import com.wearables.consent.application.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/preferences/notifications")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    /**
     * GET /api/v1/preferences/notifications
     *
     * <p>Return push and quiet-hours preferences for the authenticated user.
     * If the user has no saved preferences, sensible defaults are returned
     * (push on, DND off, alerts and reminders on, marketing off) without
     * creating a database row.
     */
    @GetMapping
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(preferenceService.getPreferences(userId));
    }

    /**
     * PUT /api/v1/preferences/notifications
     *
     * <p>Upsert notification preferences for the authenticated user. The full
     * preference object must be supplied — partial updates are not supported.
     */
    @PutMapping
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
            @Valid @RequestBody UpdateNotificationPreferenceRequest req,
            Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(preferenceService.updatePreferences(userId, req));
    }
}
