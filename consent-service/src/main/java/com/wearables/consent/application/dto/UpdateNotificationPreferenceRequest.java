/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.application.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateNotificationPreferenceRequest(

        boolean pushEnabled,
        boolean quietHoursEnabled,

        /** Local start time in HH:mm format, e.g. "22:00". Required when quietHoursEnabled is true. */
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
                 message = "must be in HH:mm format (00:00 – 23:59)")
        String quietHoursStart,

        /** Local end time in HH:mm format, e.g. "07:00". Required when quietHoursEnabled is true. */
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
                 message = "must be in HH:mm format (00:00 – 23:59)")
        String quietHoursEnd,

        boolean dndEnabled,
        boolean alertsEnabled,
        boolean remindersEnabled,
        boolean marketingEnabled
) {}
