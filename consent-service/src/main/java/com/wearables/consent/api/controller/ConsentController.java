/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.api.controller;

import com.wearables.consent.application.dto.ConsentResponse;
import com.wearables.consent.application.dto.UpdateConsentRequest;
import com.wearables.consent.application.service.ConsentService;
import com.wearables.consent.domain.entity.ConsentType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    /**
     * GET /api/v1/consents
     *
     * <p>List all consent records for the authenticated user. Returns only
     * consent types that have been explicitly granted or revoked at least once.
     */
    @GetMapping
    public ResponseEntity<List<ConsentResponse>> listConsents(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(consentService.getAllConsents(userId));
    }

    /**
     * GET /api/v1/consents/{consentType}
     *
     * <p>Fetch the current status of a specific consent type.
     *
     * @return 404 if the user has never interacted with this consent type.
     */
    @GetMapping("/{consentType}")
    public ResponseEntity<ConsentResponse> getConsent(
            @PathVariable ConsentType consentType,
            Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(consentService.getConsent(userId, consentType));
    }

    /**
     * PUT /api/v1/consents/{consentType}
     *
     * <p>Grant or revoke a consent type. Idempotent — re-granting an already-GRANTED
     * consent simply increments the version and refreshes the timestamp.
     *
     * <p>The client IP is captured server-side for India DPDP Act audit trails;
     * callers do not supply it in the request body.
     */
    @PutMapping("/{consentType}")
    public ResponseEntity<ConsentResponse> updateConsent(
            @PathVariable ConsentType consentType,
            @Valid @RequestBody UpdateConsentRequest req,
            Authentication auth,
            HttpServletRequest httpRequest) {
        UUID userId = UUID.fromString(auth.getName());
        String ip = resolveClientIp(httpRequest);
        return ResponseEntity.ok(consentService.updateConsent(userId, consentType, req, ip));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim()
                                               : request.getRemoteAddr();
    }
}
