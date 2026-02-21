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
import com.wearables.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long   ACCESS_TOKEN_TTL_SECONDS = 3_600L;          // 1 hour
    private static final long   REFRESH_TOKEN_TTL_DAYS   = 7L;
    private static final int    MAX_FAILED_ATTEMPTS       = 5;
    private static final long   LOCKOUT_SECONDS           = 900L;            // 15 min
    private static final String REFRESH_KEY_PREFIX        = "auth:refresh:";

    private final UserCredentialRepository repository;
    private final BCryptPasswordEncoder    encoder;
    private final StringRedisTemplate      redis;

    /**
     * JWT utility initialised lazily after the secret is injected from config.
     * The secret must be ≥32 bytes (256 bits) for HMAC-SHA256.
     */
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    public void setJwtSecret(String secret) {
        this.jwtUtil = new JwtUtil(secret);
    }

    // -------------------------------------------------------------------------
    // Credential management
    // -------------------------------------------------------------------------

    public void createCredential(UUID userId, String password) {
        repository.save(UserCredential.builder()
                .userId(userId)
                .passwordHash(encoder.encode(password))
                .passwordAlgo("bcrypt")
                .passwordUpdatedAt(Instant.now())
                .failedLoginAttempts(0)
                .build());
    }

    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        UserCredential cred = findOrThrow(userId);

        if (!encoder.matches(currentPassword, cred.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        cred.setPasswordHash(encoder.encode(newPassword));
        cred.setPasswordAlgo("bcrypt");
        cred.setPasswordUpdatedAt(Instant.now());
        cred.setFailedLoginAttempts(0);
        cred.setLockedUntil(null);
        repository.save(cred);
    }

    // -------------------------------------------------------------------------
    // Login / token lifecycle
    // -------------------------------------------------------------------------

    public TokenResponse login(UUID userId, String password) {
        UserCredential cred = findOrThrow(userId);

        if (cred.getLockedUntil() != null && cred.getLockedUntil().isAfter(Instant.now())) {
            throw new IllegalStateException("Account is temporarily locked. Try again later.");
        }

        if (!encoder.matches(password, cred.getPasswordHash())) {
            int attempts = cred.getFailedLoginAttempts() + 1;
            cred.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                cred.setLockedUntil(Instant.now().plusSeconds(LOCKOUT_SECONDS));
            }
            repository.save(cred);
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Reset failure counter on successful login
        cred.setFailedLoginAttempts(0);
        cred.setLockedUntil(null);
        repository.save(cred);

        return buildTokenPair(userId.toString());
    }

    /**
     * Token refresh with rotation: the supplied refresh token is invalidated and
     * a new token pair is returned.
     */
    public TokenResponse refresh(String refreshToken) {
        String userId = redis.opsForValue().get(REFRESH_KEY_PREFIX + refreshToken);
        if (userId == null) {
            throw new IllegalArgumentException("Refresh token is invalid or has expired");
        }

        // Rotate: delete old, issue new pair
        redis.delete(REFRESH_KEY_PREFIX + refreshToken);
        return buildTokenPair(userId);
    }

    /**
     * Server-side logout: invalidate the refresh token.
     * The access token remains valid until it expires naturally.
     */
    public void logout(String refreshToken) {
        redis.delete(REFRESH_KEY_PREFIX + refreshToken);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private UserCredential findOrThrow(UUID userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private TokenResponse buildTokenPair(String userId) {
        String accessToken  = jwtUtil.generateToken(userId, Map.of("roles", "USER"), ACCESS_TOKEN_TTL_SECONDS);
        String refreshToken = UUID.randomUUID().toString();

        redis.opsForValue().set(
                REFRESH_KEY_PREFIX + refreshToken,
                userId,
                Duration.ofDays(REFRESH_TOKEN_TTL_DAYS)
        );

        return new TokenResponse(accessToken, refreshToken, ACCESS_TOKEN_TTL_SECONDS);
    }
}
