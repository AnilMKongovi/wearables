/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Kafka event published to the {@code warranty.events} topic on warranty and
 * claim lifecycle transitions.
 *
 * <p>Event types:
 * <ul>
 *   <li>{@code WARRANTY_REGISTERED}  — new warranty submitted</li>
 *   <li>{@code WARRANTY_VERIFIED}    — proof-of-purchase confirmed</li>
 *   <li>{@code WARRANTY_EXPIRING}    — expiry within threshold (30 / 7 / 1 day)</li>
 *   <li>{@code WARRANTY_EXPIRED}     — warranty period elapsed</li>
 *   <li>{@code WARRANTY_VOID}        — warranty invalidated</li>
 *   <li>{@code CLAIM_OPENED}         — new claim raised</li>
 *   <li>{@code CLAIM_RESOLVED}       — claim closed with resolution</li>
 * </ul>
 *
 * <p>Consumers: notification-service (push alerts), scheduler-service (expiry sweep).
 */
public record WarrantyEvent(
        UUID warrantyId,
        UUID userId,
        String eventType,
        /** Optional claim ID — set for CLAIM_* event types, null otherwise. */
        UUID claimId,
        Instant timestamp
) {}
