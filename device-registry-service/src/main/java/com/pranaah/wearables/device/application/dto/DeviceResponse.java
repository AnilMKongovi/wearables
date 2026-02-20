/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.application.dto;

import com.pranaah.wearables.device.domain.entity.Device;
import com.pranaah.wearables.device.domain.entity.DeviceStatus;
import com.pranaah.wearables.device.domain.entity.DeviceType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DeviceResponse {

    private UUID id;
    private UUID userId;
    private String deviceId;
    private String subscriptionId;
    private DeviceType deviceType;
    private DeviceStatus status;
    private String appVersion;
    private String osVersion;
    private Instant registeredAt;
    private Instant updatedAt;
    private Instant lastSeenAt;

    public static DeviceResponse from(Device device) {
        return DeviceResponse.builder()
                .id(device.getId())
                .userId(device.getUserId())
                .deviceId(device.getDeviceId())
                .subscriptionId(device.getSubscriptionId())
                .deviceType(device.getDeviceType())
                .status(device.getStatus())
                .appVersion(device.getAppVersion())
                .osVersion(device.getOsVersion())
                .registeredAt(device.getRegisteredAt())
                .updatedAt(device.getUpdatedAt())
                .lastSeenAt(device.getLastSeenAt())
                .build();
    }
}
