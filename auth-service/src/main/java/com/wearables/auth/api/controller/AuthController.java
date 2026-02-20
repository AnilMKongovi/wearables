/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.

 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
*/


package com.pranaah.wearables.auth.api.controller;

import com.platform.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/internal/credentials")
    public void createCred(@RequestBody Map<String, String> req) {
        service.createCredential(UUID.fromString(req.get("userId")), req.get("password"));
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {
        String token = service.login(UUID.fromString(req.get("userId")), req.get("password"));
        return Map.of("accessToken", token);
    }
}
