/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.client;

import java.util.UUID;

/**
 * Lightweight projection of a warranty returned by warranty-service.
 * Only the fields required by claim-service for validation are mapped.
 */
public record WarrantyInfo(
        UUID id,
        UUID userId,
        String status
) {
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
