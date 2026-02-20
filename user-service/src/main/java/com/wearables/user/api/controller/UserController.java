/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.

 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
*/


package com.pranaah.wearables.user.api.controller;

import com.platform.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, String> req) {
        UUID userId = service.createUser(
                req.get("email"),
                req.get("phone"),
                req.get("countryCode")
        );
        return Map.of("userId", userId);
    }
}


