/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.api.controller;

import com.pranaah.wearables.device.application.dto.DeviceResponse;
import com.pranaah.wearables.device.application.dto.RegisterDeviceRequest;
import com.pranaah.wearables.device.application.dto.UpdateTokenRequest;
import com.pranaah.wearables.device.application.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * POST /api/v1/devices
     *
     * <p>Register a new device or re-register an existing one (idempotent on deviceId).
     * Called by the mobile/wearable app on first launch and after app reinstall.
     *
     * @return 201 Created with the device record.
     */
    @PostMapping
    public ResponseEntity<DeviceResponse> registerDevice(
            @Valid @RequestBody RegisterDeviceRequest request) {
        DeviceResponse response = deviceService.registerDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/devices/{deviceId}/token
     *
     * <p>Refresh the push subscription token for a device.
     * Called when FCM/APNs issues a new token or the user re-enables push.
     *
     * @return 200 OK with the updated device record.
     */
    @PutMapping("/{deviceId}/token")
    public ResponseEntity<DeviceResponse> updateToken(
            @PathVariable UUID deviceId,
            @Valid @RequestBody UpdateTokenRequest request) {
        return ResponseEntity.ok(deviceService.updateToken(deviceId, request));
    }

    /**
     * DELETE /api/v1/devices/{deviceId}
     *
     * <p>Deactivate a device — it will no longer receive push notifications.
     * Called on user logout or when the user revokes push permission.
     *
     * @return 204 No Content.
     */
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deactivateDevice(@PathVariable UUID deviceId) {
        deviceService.deactivateDevice(deviceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/devices/user/{userId}
     *
     * <p>List all active devices for a user.
     * Used internally by notification-service to resolve push subscription IDs.
     *
     * @return 200 OK with the list of active devices.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceResponse>> getActiveDevicesForUser(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(deviceService.getActiveDevicesForUser(userId));
    }

    /**
     * GET /api/v1/devices/internal/user/{userId}/subscriptions
     *
     * <p><b>Internal endpoint — not routed via the API gateway.</b>
     * Returns only the push subscription IDs (FCM/APNs tokens) for all active
     * devices owned by the user. Called by notification-service to build the
     * OneSignal recipient list without transferring full device records.
     *
     * @return 200 OK — list of {@code { "subscriptionId": "..." }} objects.
     */
    @GetMapping("/internal/user/{userId}/subscriptions")
    public ResponseEntity<List<SubscriptionIdView>> getSubscriptionIds(@PathVariable UUID userId) {
        List<SubscriptionIdView> ids = deviceService.getActiveDevicesForUser(userId).stream()
                .filter(d -> d.getSubscriptionId() != null && !d.getSubscriptionId().isBlank())
                .map(d -> new SubscriptionIdView(d.getSubscriptionId()))
                .toList();
        return ResponseEntity.ok(ids);
    }

    /** Minimal projection returned by the internal subscription-ID endpoint. */
    record SubscriptionIdView(String subscriptionId) {}
}
