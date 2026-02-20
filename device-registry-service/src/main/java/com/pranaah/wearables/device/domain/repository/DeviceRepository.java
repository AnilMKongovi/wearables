/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.domain.repository;

import com.pranaah.wearables.device.domain.entity.Device;
import com.pranaah.wearables.device.domain.entity.DeviceStatus;
import com.pranaah.wearables.device.domain.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /** All devices belonging to a user, filtered by status. */
    List<Device> findByUserIdAndStatus(UUID userId, DeviceStatus status);

    /** All devices belonging to a user regardless of status. */
    List<Device> findByUserId(UUID userId);

    /**
     * Lookup by the hardware device identifier — used to detect re-registration
     * of the same physical device so we can update rather than duplicate.
     */
    Optional<Device> findByDeviceId(String deviceId);

    /** Used by notification-service to find all active subscription IDs for a user. */
    List<Device> findByUserIdAndStatusAndDeviceTypeIn(
            UUID userId, DeviceStatus status, List<DeviceType> deviceTypes);
}
