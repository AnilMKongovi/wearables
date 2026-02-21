/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.notification.application.service;

import com.wearables.notification.domain.event.AlertEvent;
import com.wearables.notification.infrastructure.client.DeviceRegistryClient;
import com.wearables.notification.infrastructure.client.OneSignalClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates the notification dispatch pipeline for a single {@link AlertEvent}:
 * <ol>
 *   <li>Resolve all active push subscription IDs for the affected user.</li>
 *   <li>Send a push notification via OneSignal (with Resilience4j retry).</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceRegistryClient deviceRegistryClient;
    private final OneSignalClient      oneSignalClient;

    public void notify(AlertEvent alert) {
        log.info("Processing alert [alertId={}, type={}, userId={}]",
                alert.alertId(), alert.alertType(), alert.userId());

        List<String> subscriptionIds = deviceRegistryClient.getSubscriptionIds(alert.userId());

        if (subscriptionIds.isEmpty()) {
            log.warn("No active devices found for userId={} — alert not delivered", alert.userId());
            return;
        }

        oneSignalClient.send(subscriptionIds, alert.title(), alert.message());
    }
}
