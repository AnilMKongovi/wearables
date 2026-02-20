/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.

 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
*/


package com.pranaah.wearables.auth.application.service;

import com.platform.auth.domain.entity.UserCredential;
import com.platform.auth.domain.repository.UserCredentialRepository;
import com.platform.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository repository;
    private final JwtUtil jwtUtil = new JwtUtil("very-secret-key-very-secret-key");
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        UserCredential cred = repository.findById(userId).orElseThrow();

        if (!encoder.matches(password, cred.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(userId.toString(), Map.of("roles", "USER"), 3600);
    }
}
