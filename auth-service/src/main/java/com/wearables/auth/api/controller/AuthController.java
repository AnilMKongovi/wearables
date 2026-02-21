/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.auth.api.controller;

import com.wearables.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Called by user-service immediately after a user record is created,
     * to store a hashed password credential.
     */
    @PostMapping("/internal/credentials")
    public ResponseEntity<Void> createCredential(@RequestBody Map<String, String> req) {
        authService.createCredential(UUID.fromString(req.get("userId")), req.get("password"));
        return ResponseEntity.noContent().build();
    }

    /**
     * Password-based login — returns a short-lived JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> req) {
        String token = authService.login(UUID.fromString(req.get("userId")), req.get("password"));
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
