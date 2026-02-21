/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.ingestion.application.service;

import com.wearables.ingestion.domain.event.DeviceEvent;
import com.wearables.ingestion.dto.IngestEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventIngestionService {

    static final String DEVICE_EVENTS_TOPIC = "device.events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Validates and publishes an incoming telemetry event to the {@code device.events}
     * Kafka topic for downstream processing by decision-service and analytics.
     *
     * @param request  parsed and validated request body
     * @param userId   caller's UUID, extracted from the JWT subject claim
     */
    public void ingest(IngestEventRequest request, UUID userId) {
        DeviceEvent event = new DeviceEvent(
                UUID.randomUUID(),
                request.deviceId(),
                userId,
                request.eventType().name(),
                request.payload(),
                Instant.now()
        );

        kafkaTemplate.send(DEVICE_EVENTS_TOPIC, event.deviceId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish DeviceEvent [deviceId={}, type={}]",
                                event.deviceId(), event.eventType(), ex);
                    } else {
                        log.debug("Published DeviceEvent [eventId={}, deviceId={}, type={}]",
                                event.eventId(), event.deviceId(), event.eventType());
                    }
                });
    }
}
