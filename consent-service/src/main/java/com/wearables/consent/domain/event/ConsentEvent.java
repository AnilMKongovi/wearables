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
 * Kafka event published to the {@code consent.events} topic on every
 * consent state change.
 *
 * <p>Event types:
 * <ul>
 *   <li>{@code CONSENT_GRANTED} — user granted a consent type</li>
 *   <li>{@code CONSENT_REVOKED} — user revoked a previously granted consent</li>
 * </ul>
 *
 * <p>Consumers: notification-service (re-check push opt-in before sending),
 * decision-service (gate data processing on regulatory consent).
 */
public record ConsentEvent(
        UUID userId,
        String consentType,
        String eventType,
        int version,
        Instant timestamp
) {}
