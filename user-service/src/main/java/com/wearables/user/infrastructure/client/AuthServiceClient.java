/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.user.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * HTTP client for inter-service calls to auth-service.
 *
 * <p>Used to notify auth-service when a user account is deactivated so that
 * active sessions / refresh tokens can be revoked.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @Value("${clients.auth-service.base-url}")
    private String authServiceBaseUrl;

    /**
     * Signals auth-service to revoke all active sessions for the given user.
     */
    public void revokeUserSessions(UUID userId) {
        String url = authServiceBaseUrl + "/internal/sessions/" + userId + "/revoke";
        try {
            restTemplate.delete(url);
            log.info("Revoked sessions for user {}", userId);
        } catch (Exception ex) {
            log.warn("Failed to revoke sessions for user {} — auth-service may be unavailable: {}", userId, ex.getMessage());
        }
    }
}
