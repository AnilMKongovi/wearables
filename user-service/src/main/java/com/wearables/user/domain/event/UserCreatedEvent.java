/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.user.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Kafka event published on the {@code user.created} topic when a new user
 * is successfully registered.
 *
 * <p>Consumers (e.g. notification-service, consent-service) can subscribe to
 * bootstrap per-user state.
 */
public record UserCreatedEvent(
        UUID userId,
        String email,
        String phone,
        String countryCode,
        Instant createdAt
) {}
