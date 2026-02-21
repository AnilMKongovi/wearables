/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Inbound event published by user-service on the {@code user.created} Kafka topic
 * whenever a new user account is successfully registered.
 *
 * <p>consent-service subscribes to this topic to eagerly seed per-user
 * consent and notification-preference rows with safe defaults.
 */
public record UserCreatedEvent(
        UUID userId,
        String email,
        String phone,
        String countryCode,
        Instant createdAt
) {}
