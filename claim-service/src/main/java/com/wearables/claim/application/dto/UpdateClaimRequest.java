/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.application.dto;

import com.wearables.claim.domain.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateClaimRequest(
        @NotNull ClaimStatus status,

        /** Resolution or review notes — required when transitioning to RESOLVED or REJECTED. */
        @Size(max = 2000) String resolutionNotes
) {}
