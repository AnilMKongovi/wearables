/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.notification.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

/**
 * HTTP client for device-registry-service's internal endpoint.
 *
 * <p>Fetches the push subscription IDs (FCM/APNs tokens) for all active
 * devices registered to a user. Called synchronously before dispatching
 * a OneSignal notification.
 *
 * <p>The {@code /internal/} path is accessible without a JWT and should
 * only be reachable from within the private service network (not exposed
 * via the API gateway).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRegistryClient {

    @Qualifier("deviceRegistryWebClient")
    private final WebClient webClient;

    /**
     * Returns the push subscription IDs for all active mobile/wearable devices
     * belonging to the given user. Returns an empty list if the user has no
     * active devices or if the registry is unreachable.
     */
    public List<String> getSubscriptionIds(UUID userId) {
        try {
            SubscriptionIdResponse[] responses = webClient.get()
                    .uri("/api/v1/devices/internal/user/{userId}/subscriptions", userId)
                    .retrieve()
                    .bodyToMono(SubscriptionIdResponse[].class)
                    .block();

            if (responses == null) return List.of();

            return List.of(responses).stream()
                    .map(SubscriptionIdResponse::subscriptionId)
                    .filter(id -> id != null && !id.isBlank())
                    .toList();

        } catch (Exception ex) {
            log.error("Failed to fetch subscription IDs for userId={}", userId, ex);
            return List.of();
        }
    }

    /** Minimal DTO matching the internal endpoint response. */
    private record SubscriptionIdResponse(String subscriptionId) {}
}
