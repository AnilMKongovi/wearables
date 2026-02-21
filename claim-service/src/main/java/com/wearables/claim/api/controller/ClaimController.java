/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.api.controller;

import com.wearables.claim.application.dto.ClaimResponse;
import com.wearables.claim.application.dto.OpenClaimRequest;
import com.wearables.claim.application.dto.UpdateClaimRequest;
import com.wearables.claim.application.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warranties/{warrantyId}/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    /**
     * POST /api/v1/warranties/{warrantyId}/claims
     *
     * <p>Open a new warranty claim. Only the warranty owner may raise a claim,
     * and the warranty must be in ACTIVE status.
     *
     * @return 201 Created with the new claim record.
     */
    @PostMapping
    public ResponseEntity<ClaimResponse> openClaim(
            @PathVariable UUID warrantyId,
            @Valid @RequestBody OpenClaimRequest req,
            Authentication auth,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimService.openClaim(warrantyId, req, userId, authHeader));
    }

    /**
     * GET /api/v1/warranties/{warrantyId}/claims
     *
     * <p>List all claims raised against a warranty.
     */
    @GetMapping
    public ResponseEntity<List<ClaimResponse>> listClaims(@PathVariable UUID warrantyId) {
        return ResponseEntity.ok(claimService.getClaimsByWarrantyId(warrantyId));
    }

    /**
     * GET /api/v1/warranties/{warrantyId}/claims/{claimId}
     *
     * <p>Fetch a specific claim by ID.
     */
    @GetMapping("/{claimId}")
    public ResponseEntity<ClaimResponse> getClaim(
            @PathVariable UUID warrantyId,
            @PathVariable UUID claimId) {
        return ResponseEntity.ok(claimService.getClaimById(warrantyId, claimId));
    }

    /**
     * PUT /api/v1/warranties/{warrantyId}/claims/{claimId}
     *
     * <p>Update a claim's status and/or resolution notes.
     * Intended for admin / support agents.
     */
    @PutMapping("/{claimId}")
    public ResponseEntity<ClaimResponse> updateClaim(
            @PathVariable UUID warrantyId,
            @PathVariable UUID claimId,
            @Valid @RequestBody UpdateClaimRequest req) {
        return ResponseEntity.ok(claimService.updateClaim(warrantyId, claimId, req));
    }
}
