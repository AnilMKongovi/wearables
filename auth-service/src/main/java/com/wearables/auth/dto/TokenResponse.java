/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.auth.dto;

/**
 * Returned on successful login or token refresh.
 *
 * @param accessToken  Short-lived JWT (1 hour) for API calls.
 * @param refreshToken Opaque token (7 days) stored in Redis; used to obtain new access tokens.
 * @param expiresIn    Access token lifetime in seconds.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}
