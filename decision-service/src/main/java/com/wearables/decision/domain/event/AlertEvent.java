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
import java.util.UUID;

/**
 * Published to the {@code alert.events} Kafka topic when the rule engine
 * determines that a wearable event requires user notification.
 *
 * <p>Consumed by notification-service to dispatch a push notification.
 */
public record AlertEvent(
        UUID alertId,
        String deviceId,
        UUID userId,
        String alertType,
        String title,
        String message,
        Instant timestamp
) {}
