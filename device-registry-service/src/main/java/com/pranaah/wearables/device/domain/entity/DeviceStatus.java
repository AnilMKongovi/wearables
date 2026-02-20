/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.domain.entity;

public enum DeviceStatus {

    /** Device is registered and eligible to receive push notifications. */
    ACTIVE,

    /** Device has been deregistered (user logout, token invalidation, etc.). */
    INACTIVE
}
