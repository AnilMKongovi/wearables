/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.notification.application.consumer;

import com.wearables.notification.application.service.NotificationService;
import com.wearables.notification.domain.event.AlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for the {@code alert.events} topic.
 *
 * <p>Each consumed {@link AlertEvent} is handed off to {@link NotificationService}
 * which resolves the user's device tokens and dispatches a push notification
 * via OneSignal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventConsumer {

    static final String ALERT_EVENTS_TOPIC = "alert.events";

    private final NotificationService notificationService;

    @KafkaListener(topics = ALERT_EVENTS_TOPIC, groupId = "notification-service")
    public void consume(AlertEvent alert) {
        log.debug("Received AlertEvent [alertId={}, type={}, userId={}]",
                alert.alertId(), alert.alertType(), alert.userId());

        try {
            notificationService.notify(alert);
        } catch (Exception ex) {
            // Log and continue — the Kafka offset is committed regardless so we
            // don't cause an infinite retry loop. A DLQ strategy should be added
            // for production-grade reliability.
            log.error("Failed to process AlertEvent [alertId={}]", alert.alertId(), ex);
        }
    }
}
