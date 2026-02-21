/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.application.service;

import com.wearables.claim.application.dto.ClaimResponse;
import com.wearables.claim.application.dto.OpenClaimRequest;
import com.wearables.claim.application.dto.UpdateClaimRequest;
import com.wearables.claim.client.WarrantyClient;
import com.wearables.claim.client.WarrantyInfo;
import com.wearables.claim.domain.entity.ClaimStatus;
import com.wearables.claim.domain.entity.WarrantyClaim;
import com.wearables.claim.domain.event.ClaimEvent;
import com.wearables.claim.domain.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {

    static final String WARRANTY_EVENTS_TOPIC = "warranty.events";

    private final ClaimRepository claimRepository;
    private final WarrantyClient warrantyClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // -------------------------------------------------------------------------
    // Open
    // -------------------------------------------------------------------------

    @Transactional
    public ClaimResponse openClaim(UUID warrantyId, OpenClaimRequest req, UUID userId, String authHeader) {
        WarrantyInfo warranty = warrantyClient.getWarranty(warrantyId, authHeader);

        // Enforce ownership
        if (!warranty.userId().equals(userId)) {
            throw new IllegalArgumentException("Warranty does not belong to the requesting user");
        }

        // Only ACTIVE warranties can have a claim raised
        if (!warranty.isActive()) {
            throw new IllegalStateException(
                    "Claims can only be raised on ACTIVE warranties. Current status: "
                    + warranty.status());
        }

        WarrantyClaim claim = WarrantyClaim.builder()
                .id(UUID.randomUUID())
                .warrantyId(warrantyId)
                .userId(userId)
                .claimType(req.claimType())
                .status(ClaimStatus.OPEN)
                .description(req.description())
                .openedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        claim = claimRepository.save(claim);
        publishEvent(warrantyId, userId, "CLAIM_OPENED", claim.getId());
        log.info("Claim opened [claimId={}, warrantyId={}, userId={}]",
                claim.getId(), warrantyId, userId);

        return ClaimResponse.from(claim);
    }

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByWarrantyId(UUID warrantyId) {
        return claimRepository.findByWarrantyId(warrantyId).stream()
                .map(ClaimResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClaimResponse getClaimById(UUID warrantyId, UUID claimId) {
        return ClaimResponse.from(findOrThrow(warrantyId, claimId));
    }

    // -------------------------------------------------------------------------
    // Update (admin or agent action)
    // -------------------------------------------------------------------------

    @Transactional
    public ClaimResponse updateClaim(UUID warrantyId, UUID claimId, UpdateClaimRequest req) {
        WarrantyClaim claim = findOrThrow(warrantyId, claimId);

        if (claim.getStatus() == ClaimStatus.CLOSED) {
            throw new IllegalStateException("Cannot update a CLOSED claim: " + claimId);
        }

        claim.setStatus(req.status());
        if (req.resolutionNotes() != null) {
            claim.setResolutionNotes(req.resolutionNotes());
        }

        boolean terminalStatus = req.status() == ClaimStatus.RESOLVED
                || req.status() == ClaimStatus.CLOSED
                || req.status() == ClaimStatus.REJECTED;
        if (terminalStatus && claim.getResolvedAt() == null) {
            claim.setResolvedAt(Instant.now());
        }

        claim.setUpdatedAt(Instant.now());
        claim = claimRepository.save(claim);

        if (req.status() == ClaimStatus.RESOLVED || req.status() == ClaimStatus.CLOSED) {
            publishEvent(warrantyId, claim.getUserId(), "CLAIM_RESOLVED", claimId);
        }

        log.info("Claim updated [claimId={}, newStatus={}]", claimId, req.status());
        return ClaimResponse.from(claim);
    }

    // -------------------------------------------------------------------------

    private WarrantyClaim findOrThrow(UUID warrantyId, UUID claimId) {
        return claimRepository.findByIdAndWarrantyId(claimId, warrantyId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Claim not found: " + claimId + " for warranty: " + warrantyId));
    }

    private void publishEvent(UUID warrantyId, UUID userId, String eventType, UUID claimId) {
        ClaimEvent event = new ClaimEvent(warrantyId, userId, eventType, claimId, Instant.now());
        kafkaTemplate.send(WARRANTY_EVENTS_TOPIC, warrantyId.toString(), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ClaimEvent [type={}, warrantyId={}]",
                                eventType, warrantyId, ex);
                    }
                });
    }
}
