/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.ingestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Payload sent by a mobile app, BLE gateway, or device cloud to report a wearable event.
 *
 * <p>The {@code userId} is NOT accepted from the caller — it is extracted from the validated
 * JWT to prevent spoofing.
 */
public record IngestEventRequest(
        /** Hardware identifier of the source device (as registered in device-registry). */
        @NotBlank String deviceId,

        /** Category of the event being reported. */
        @NotNull EventType eventType,

        /**
         * Structured metric values for this event type.
         * E.g. {@code { "heartRate": 145 }} for {@code HEART_RATE}.
         */
        @NotNull Map<String, Object> payload
) {}
