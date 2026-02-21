/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.domain.entity;

public enum WarrantyStatus {
    /** Submitted; awaiting proof-of-purchase verification. */
    PENDING_VERIFICATION,

    /** Verified and within the warranty period. */
    ACTIVE,

    /** Warranty period has elapsed. */
    EXPIRED,

    /** Invalidated — e.g. tampered device, unauthorized repair, or fraud. */
    VOID
}
