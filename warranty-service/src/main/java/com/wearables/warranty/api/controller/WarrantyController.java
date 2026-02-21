/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.api.controller;

import com.wearables.warranty.application.dto.RegisterWarrantyRequest;
import com.wearables.warranty.application.dto.WarrantyResponse;
import com.wearables.warranty.application.service.WarrantyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warranties")
@RequiredArgsConstructor
public class WarrantyController {

    private final WarrantyService warrantyService;

    /**
     * POST /api/v1/warranties
     *
     * <p>Register a new product warranty. The {@code userId} is extracted from
     * the JWT token — callers cannot register warranties on behalf of other users.
     *
     * @return 201 Created with the full warranty record.
     */
    @PostMapping
    public ResponseEntity<WarrantyResponse> register(
            @Valid @RequestBody RegisterWarrantyRequest req,
            Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(warrantyService.registerWarranty(req, userId));
    }

    /**
     * GET /api/v1/warranties
     *
     * <p>List all warranties belonging to the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<WarrantyResponse>> listMine(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(warrantyService.getByUserId(userId));
    }

    /**
     * GET /api/v1/warranties/{id}
     *
     * <p>Fetch a warranty by its UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WarrantyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(warrantyService.getById(id));
    }

    /**
     * GET /api/v1/warranties/serial/{serialNumber}
     *
     * <p>Lookup a warranty by the device's hardware serial number.
     */
    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<WarrantyResponse> getBySerialNumber(@PathVariable String serialNumber) {
        return ResponseEntity.ok(warrantyService.getBySerialNumber(serialNumber));
    }

    /**
     * PUT /api/v1/warranties/{id}/verify
     *
     * <p>Mark proof-of-purchase as verified and set the warranty to ACTIVE.
     * Intended for admin / support agents.
     */
    @PutMapping("/{id}/verify")
    public ResponseEntity<WarrantyResponse> verify(@PathVariable UUID id) {
        return ResponseEntity.ok(warrantyService.verify(id));
    }

    /**
     * PUT /api/v1/warranties/{id}/void
     *
     * <p>Invalidate a warranty (e.g. tampered device, fraudulent claim).
     * Intended for admin / support agents.
     */
    @PutMapping("/{id}/void")
    public ResponseEntity<WarrantyResponse> voidWarranty(@PathVariable UUID id) {
        return ResponseEntity.ok(warrantyService.voidWarranty(id));
    }
}
