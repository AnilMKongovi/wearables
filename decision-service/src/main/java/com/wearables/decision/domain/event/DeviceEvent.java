/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.decision.domain.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Consumed from the {@code device.events} Kafka topic.
 * Schema matches the record published by event-ingestion-service.
 */
public record DeviceEvent(
        UUID eventId,
        String deviceId,
        UUID userId,
        String eventType,
        Map<String, Object> payload,
        Instant timestamp
) {}
