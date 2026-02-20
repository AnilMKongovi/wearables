/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.user.api.controller;

import com.wearables.user.application.dto.CreateUserRequest;
import com.wearables.user.application.dto.UpdatePreferencesRequest;
import com.wearables.user.application.dto.UpdateProfileRequest;
import com.wearables.user.application.dto.UserResponse;
import com.wearables.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Register a new user.
     * Called by auth-service after identity verification — does not require a JWT.
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Fetch the current user's full profile using the subject claim from the JWT.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(userService.getUserById(UUID.fromString(userId)));
    }

    /**
     * Fetch any user by ID (typically used by internal services).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Update the authenticated user's profile fields.
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable UUID id,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    /**
     * Update the authenticated user's notification and display preferences.
     */
    @PutMapping("/{id}/preferences")
    public ResponseEntity<UserResponse> updatePreferences(
            @PathVariable UUID id,
            @RequestBody UpdatePreferencesRequest request) {
        return ResponseEntity.ok(userService.updatePreferences(id, request));
    }

    /**
     * Soft-delete (deactivate) a user account.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
