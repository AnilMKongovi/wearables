/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.application.service;

import com.pranaah.wearables.device.application.dto.DeviceResponse;
import com.pranaah.wearables.device.application.dto.RegisterDeviceRequest;
import com.pranaah.wearables.device.application.dto.UpdateTokenRequest;
import com.pranaah.wearables.device.domain.entity.Device;
import com.pranaah.wearables.device.domain.entity.DeviceStatus;
import com.pranaah.wearables.device.domain.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    /**
     * Registers a device for the given user.
     *
     * <p>If the hardware {@code deviceId} is already known, the existing record is
     * updated (re-registration after app reinstall or OS upgrade) rather than
     * creating a duplicate. Otherwise a new record is persisted.
     */
    @Transactional
    public DeviceResponse registerDevice(RegisterDeviceRequest request) {
        Device device = deviceRepository.findByDeviceId(request.getDeviceId())
                .map(existing -> {
                    existing.setUserId(request.getUserId());
                    existing.setSubscriptionId(request.getSubscriptionId());
                    existing.setDeviceType(request.getDeviceType());
                    existing.setStatus(DeviceStatus.ACTIVE);
                    existing.setAppVersion(request.getAppVersion());
                    existing.setOsVersion(request.getOsVersion());
                    existing.setUpdatedAt(Instant.now());
                    existing.setLastSeenAt(Instant.now());
                    return existing;
                })
                .orElseGet(() -> Device.builder()
                        .id(UUID.randomUUID())
                        .userId(request.getUserId())
                        .deviceId(request.getDeviceId())
                        .subscriptionId(request.getSubscriptionId())
                        .deviceType(request.getDeviceType())
                        .status(DeviceStatus.ACTIVE)
                        .appVersion(request.getAppVersion())
                        .osVersion(request.getOsVersion())
                        .registeredAt(Instant.now())
                        .updatedAt(Instant.now())
                        .lastSeenAt(Instant.now())
                        .build());

        return DeviceResponse.from(deviceRepository.save(device));
    }

    /**
     * Updates the push subscription token for a device.
     * Called when FCM/APNs rotates the token or the user re-grants push permission.
     */
    @Transactional
    public DeviceResponse updateToken(UUID deviceId, UpdateTokenRequest request) {
        Device device = findActiveOrThrow(deviceId);
        device.setSubscriptionId(request.getSubscriptionId());
        if (request.getAppVersion() != null) {
            device.setAppVersion(request.getAppVersion());
        }
        device.setUpdatedAt(Instant.now());
        device.setLastSeenAt(Instant.now());
        return DeviceResponse.from(deviceRepository.save(device));
    }

    /**
     * Deactivates a device so it no longer receives push notifications.
     * Called on user logout, account deletion, or explicit opt-out.
     */
    @Transactional
    public void deactivateDevice(UUID deviceId) {
        Device device = findActiveOrThrow(deviceId);
        device.setStatus(DeviceStatus.INACTIVE);
        device.setUpdatedAt(Instant.now());
        deviceRepository.save(device);
    }

    /**
     * Returns all active devices registered for the given user.
     * Used by notification-service to build the push recipient list.
     */
    @Transactional(readOnly = true)
    public List<DeviceResponse> getActiveDevicesForUser(UUID userId) {
        return deviceRepository
                .findByUserIdAndStatus(userId, DeviceStatus.ACTIVE)
                .stream()
                .map(DeviceResponse::from)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------

    private Device findActiveOrThrow(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new NoSuchElementException("Device not found: " + deviceId));
        if (device.getStatus() == DeviceStatus.INACTIVE) {
            throw new IllegalStateException("Device is already inactive: " + deviceId);
        }
        return device;
    }
}
