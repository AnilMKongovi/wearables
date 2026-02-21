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
import com.wearables.auth.dto.ChangePasswordRequest;
import com.wearables.auth.dto.CreateCredentialRequest;
import com.wearables.auth.dto.LoginRequest;
import com.wearables.auth.dto.RefreshTokenRequest;
import com.wearables.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Internal endpoint: called by user-service after a new user record is created
     * to store an initial hashed-password credential.
     */
    @PostMapping("/internal/credentials")
    public ResponseEntity<Void> createCredential(@Valid @RequestBody CreateCredentialRequest req) {
        authService.createCredential(req.userId(), req.password());
        return ResponseEntity.noContent().build();
    }

    /**
     * Password-based login. Returns a short-lived access token and a long-lived
     * refresh token (stored server-side in Redis).
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req.userId(), req.password()));
    }

    /**
     * Exchange a valid refresh token for a new token pair (refresh token rotation).
     * The supplied refresh token is invalidated; a new one is returned.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(authService.refresh(req.refreshToken()));
    }

    /**
     * Invalidate the given refresh token (server-side logout).
     * The corresponding access token remains valid until it expires.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest req) {
        authService.logout(req.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * Change a user's password. Requires the current password for verification.
     * All existing refresh tokens for the user remain valid; callers should
     * separately call /logout if session invalidation is desired.
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(req.userId(), req.currentPassword(), req.newPassword());
        return ResponseEntity.noContent().build();
    }
}
