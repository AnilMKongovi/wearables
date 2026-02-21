/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.ingestion.domain.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka event published to the {@code device.events} topic when a wearable
 * device reports a telemetry reading.
 *
 * <p>Consumers (decision-service, analytics pipeline) subscribe to this topic
 * to evaluate rules and trigger downstream actions.
 */
public record DeviceEvent(
        UUID eventId,
        String deviceId,
        UUID userId,
        String eventType,
        Map<String, Object> payload,
        Instant timestamp
) {}
