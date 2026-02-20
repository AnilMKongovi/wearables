/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.

 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
*/


package com.pranaah.wearables.user.application.service;

import com.platform.user.domain.entity.User;
import com.platform.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public UUID createUser(String email, String phone, String countryCode) {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .phone(phone)
                .countryCode(countryCode)
                .status(User.Status.ACTIVE)
                .emailVerified(false)
                .phoneVerified(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        repository.save(user);
        return user.getId();
    }
}


