/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.user.domain.repository;

import com.wearables.user.domain.entity.UserDevices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDevicesRepository extends JpaRepository<UserDevices, UUID> {

    List<UserDevices> findByUserId(UUID userId);

    /** Returns only active (non-deactivated) device references for a user. */
    List<UserDevices> findByUserIdAndDeactivatedAtIsNull(UUID userId);

    Optional<UserDevices> findByDeviceId(String deviceId);
}
