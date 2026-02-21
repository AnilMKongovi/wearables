/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Kafka event published to the {@code warranty.events} topic on claim
 * lifecycle transitions.
 *
 * <p>Event types:
 * <ul>
 *   <li>{@code CLAIM_OPENED}   — new claim raised against an active warranty</li>
 *   <li>{@code CLAIM_RESOLVED} — claim closed with a resolution</li>
 * </ul>
 *
 * <p>Consumers: notification-service (push alerts).
 */
public record ClaimEvent(
        UUID warrantyId,
        UUID userId,
        String eventType,
        UUID claimId,
        Instant timestamp
) {}
