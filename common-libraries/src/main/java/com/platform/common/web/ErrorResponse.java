/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.platform.common.web;

import java.time.Instant;

/**
 * Canonical error payload returned by all platform services.
 *
 * <pre>
 * {
 *   "status":    400,
 *   "error":     "Bad Request",
 *   "message":   "Email already registered: foo@example.com",
 *   "path":      "/api/v1/users",
 *   "timestamp": "2026-01-01T00:00:00Z"
 * }
 * </pre>
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now());
    }
}
