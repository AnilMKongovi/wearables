/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.application.dto;

import com.pranaah.wearables.device.domain.entity.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterDeviceRequest {

    @NotNull
    private UUID userId;

    /**
     * Hardware/OS device identifier. Must be stable across app reinstalls where
     * possible (Android ANDROID_ID, iOS identifierForVendor, or a client-generated UUID).
     */
    @NotBlank
    private String deviceId;

    /**
     * OneSignal player_id or raw FCM/APNs token.
     * Optional for wearables that inherit push via a paired mobile device.
     */
    private String subscriptionId;

    @NotNull
    private DeviceType deviceType;

    private String appVersion;

    private String osVersion;
}
