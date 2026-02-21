/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.auth.application.service;

import com.platform.common.security.JwtUtil;
import com.wearables.auth.domain.entity.UserCredential;
import com.wearables.auth.domain.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;
    private final BCryptPasswordEncoder encoder;

    /**
     * JWT utility initialised lazily once the secret is injected from config.
     * The secret must be ≥32 bytes (256 bits) for HMAC-SHA256.
     */
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    public void setJwtSecret(String secret) {
        this.jwtUtil = new JwtUtil(secret);
    }

    public void createCredential(UUID userId, String password) {
        repository.save(UserCredential.builder()
                .userId(userId)
                .passwordHash(encoder.encode(password))
                .passwordAlgo("bcrypt")
                .passwordUpdatedAt(Instant.now())
                .failedLoginAttempts(0)
                .build());
    }

    public String login(UUID userId, String password) {
        UserCredential cred = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (cred.getLockedUntil() != null && cred.getLockedUntil().isAfter(Instant.now())) {
            throw new IllegalStateException("Account is temporarily locked");
        }

        if (!encoder.matches(password, cred.getPasswordHash())) {
            cred.setFailedLoginAttempts(cred.getFailedLoginAttempts() + 1);
            if (cred.getFailedLoginAttempts() >= 5) {
                cred.setLockedUntil(Instant.now().plusSeconds(900)); // 15 min lockout
            }
            repository.save(cred);
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Reset failure counter on successful login
        cred.setFailedLoginAttempts(0);
        cred.setLockedUntil(null);
        repository.save(cred);

        return jwtUtil.generateToken(userId.toString(), Map.of("roles", "USER"), 3600);
    }
}
