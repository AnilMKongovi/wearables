/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.decision.application.consumer;

import com.wearables.decision.application.service.RuleEngine;
import com.wearables.decision.domain.event.AlertEvent;
import com.wearables.decision.domain.event.DeviceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Kafka consumer for the {@code device.events} topic.
 *
 * <p>Each received {@link DeviceEvent} is evaluated by the {@link RuleEngine}.
 * If a rule fires, the resulting {@link AlertEvent} is forwarded to the
 * {@code alert.events} topic for notification-service to dispatch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceEventConsumer {

    static final String DEVICE_EVENTS_TOPIC = "device.events";
    static final String ALERT_EVENTS_TOPIC  = "alert.events";

    private final RuleEngine ruleEngine;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = DEVICE_EVENTS_TOPIC, groupId = "decision-service")
    public void consume(DeviceEvent event) {
        log.debug("Received DeviceEvent [eventId={}, type={}, deviceId={}]",
                event.eventId(), event.eventType(), event.deviceId());

        Optional<AlertEvent> alert = ruleEngine.evaluate(event);

        alert.ifPresent(a -> kafkaTemplate.send(ALERT_EVENTS_TOPIC, a.userId().toString(), a)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish AlertEvent [alertId={}, type={}]",
                                a.alertId(), a.alertType(), ex);
                    } else {
                        log.info("Published AlertEvent [alertId={}, type={}, userId={}]",
                                a.alertId(), a.alertType(), a.userId());
                    }
                }));
    }
}
