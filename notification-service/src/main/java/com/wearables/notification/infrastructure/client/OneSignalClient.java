/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.notification.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * Adapter for the OneSignal REST API.
 *
 * <p>Uses Resilience4j {@code @Retry} (up to 3 attempts, 2 s back-off) and
 * {@code @CircuitBreaker} (opens after 50 % failure rate over 10 calls) to
 * handle transient OneSignal outages gracefully.
 *
 * @see <a href="https://documentation.onesignal.com/reference/create-notification">OneSignal API</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OneSignalClient {

    @Qualifier("oneSignalWebClient")
    private final WebClient webClient;

    @Value("${onesignal.app-id}")
    private String appId;

    /**
     * Send a push notification to the given subscription IDs.
     *
     * @param subscriptionIds FCM/APNs tokens obtained from device-registry
     * @param title           Notification title
     * @param message         Notification body
     */
    @Retry(name = "onesignal")
    @CircuitBreaker(name = "onesignal", fallbackMethod = "sendFallback")
    public void send(List<String> subscriptionIds, String title, String message) {
        if (subscriptionIds.isEmpty()) {
            log.warn("No subscription IDs provided — skipping OneSignal notification");
            return;
        }

        Map<String, Object> payload = Map.of(
                "app_id",                  appId,
                "include_subscription_ids", subscriptionIds,
                "headings",                Map.of("en", title),
                "contents",                Map.of("en", message)
        );

        OneSignalResponse response = webClient.post()
                .uri("/notifications")
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, resp ->
                        resp.bodyToMono(String.class)
                                .map(body -> new IllegalArgumentException("OneSignal 400: " + body)))
                .bodyToMono(OneSignalResponse.class)
                .block();

        if (response != null && response.id() != null) {
            log.info("OneSignal notification sent: id={} recipients={}", response.id(), subscriptionIds.size());
        }
    }

    /**
     * Fallback invoked by Resilience4j when the circuit is open or all retries are exhausted.
     * Logs the failure; a dead-letter strategy can be added here if needed.
     */
    @SuppressWarnings("unused")
    public void sendFallback(List<String> subscriptionIds, String title, String message, Throwable ex) {
        log.error("OneSignal circuit open or retries exhausted — notification not delivered. "
                        + "title='{}' recipients={} cause={}",
                title, subscriptionIds.size(), ex.getMessage());
        // TODO: publish to a dead-letter Kafka topic for later retry
    }

    private record OneSignalResponse(
            @JsonProperty("id") String id,
            @JsonProperty("recipients") int recipients,
            @JsonProperty("errors") List<String> errors
    ) {}
}
