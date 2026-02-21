/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.warranty.application.service;

import com.wearables.warranty.application.dto.RegisterWarrantyRequest;
import com.wearables.warranty.application.dto.WarrantyResponse;
import com.wearables.warranty.domain.entity.*;
import com.wearables.warranty.domain.event.WarrantyEvent;
import com.wearables.warranty.domain.repository.WarrantyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarrantyService {

    static final String WARRANTY_EVENTS_TOPIC = "warranty.events";

    private final WarrantyRepository      warrantyRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    @Transactional
    public WarrantyResponse registerWarranty(RegisterWarrantyRequest req, UUID userId) {
        if (warrantyRepository.existsBySerialNumber(req.serialNumber())) {
            throw new IllegalArgumentException(
                    "A warranty is already registered for serial number: " + req.serialNumber());
        }

        LocalDate startDate = req.purchaseDate();
        LocalDate endDate   = startDate.plusMonths(req.warrantyDurationMonths());

        WarrantyRegistration warranty = WarrantyRegistration.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .deviceRegistryId(req.deviceRegistryId())
                .serialNumber(req.serialNumber())
                .productId(req.productId())
                .productSku(req.productSku())
                .productName(req.productName())
                .purchaseDate(req.purchaseDate())
                .warrantyStartDate(startDate)
                .warrantyEndDate(endDate)
                .warrantyDurationMonths(req.warrantyDurationMonths())
                .warrantyType(req.warrantyType())
                .status(WarrantyStatus.PENDING_VERIFICATION)
                .verificationStatus(VerificationStatus.UNVERIFIED)
                .sellerType(req.sellerType())
                .sellerName(req.sellerName())
                .orderNumber(req.orderNumber())
                .invoiceUrl(req.invoiceUrl())
                .purchasePrice(req.purchasePrice())
                .currency(req.currency())
                .purchaseCountry(req.purchaseCountry().toUpperCase())
                .registeredAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        warranty = warrantyRepository.save(warranty);
        publishEvent(warranty.getId(), userId, "WARRANTY_REGISTERED", null);
        log.info("Warranty registered [id={}, serialNumber={}, userId={}]",
                warranty.getId(), warranty.getSerialNumber(), userId);

        return WarrantyResponse.from(warranty);
    }

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public WarrantyResponse getById(UUID warrantyId) {
        return WarrantyResponse.from(findOrThrow(warrantyId));
    }

    @Transactional(readOnly = true)
    public List<WarrantyResponse> getByUserId(UUID userId) {
        return warrantyRepository.findByUserId(userId).stream()
                .map(WarrantyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarrantyResponse getBySerialNumber(String serialNumber) {
        return warrantyRepository.findBySerialNumber(serialNumber)
                .map(WarrantyResponse::from)
                .orElseThrow(() -> new NoSuchElementException(
                        "No warranty found for serial number: " + serialNumber));
    }

    // -------------------------------------------------------------------------
    // Status transitions (admin actions)
    // -------------------------------------------------------------------------

    @Transactional
    public WarrantyResponse verify(UUID warrantyId) {
        WarrantyRegistration w = findOrThrow(warrantyId);

        if (w.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Warranty is already verified: " + warrantyId);
        }

        w.setVerificationStatus(VerificationStatus.VERIFIED);
        w.setStatus(WarrantyStatus.ACTIVE);
        w.setUpdatedAt(Instant.now());
        warrantyRepository.save(w);

        publishEvent(warrantyId, w.getUserId(), "WARRANTY_VERIFIED", null);
        log.info("Warranty verified [id={}, userId={}]", warrantyId, w.getUserId());
        return WarrantyResponse.from(w);
    }

    @Transactional
    public WarrantyResponse voidWarranty(UUID warrantyId) {
        WarrantyRegistration w = findOrThrow(warrantyId);

        if (w.getStatus() == WarrantyStatus.VOID) {
            throw new IllegalStateException("Warranty is already void: " + warrantyId);
        }

        w.setStatus(WarrantyStatus.VOID);
        w.setUpdatedAt(Instant.now());
        warrantyRepository.save(w);

        publishEvent(warrantyId, w.getUserId(), "WARRANTY_VOID", null);
        log.info("Warranty voided [id={}, userId={}]", warrantyId, w.getUserId());
        return WarrantyResponse.from(w);
    }

    /**
     * Called by the scheduler-service sweep to expire warranties whose end date
     * has passed. Idempotent — already-expired warranties are silently skipped.
     */
    @Transactional
    public void expireWarranty(UUID warrantyId) {
        WarrantyRegistration w = findOrThrow(warrantyId);

        if (w.getStatus() == WarrantyStatus.EXPIRED) return;

        w.setStatus(WarrantyStatus.EXPIRED);
        w.setUpdatedAt(Instant.now());
        warrantyRepository.save(w);

        publishEvent(warrantyId, w.getUserId(), "WARRANTY_EXPIRED", null);
        log.info("Warranty expired [id={}, userId={}]", warrantyId, w.getUserId());
    }

    // -------------------------------------------------------------------------

    WarrantyRegistration findOrThrow(UUID warrantyId) {
        return warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new NoSuchElementException("Warranty not found: " + warrantyId));
    }

    void publishEvent(UUID warrantyId, UUID userId, String eventType, UUID claimId) {
        WarrantyEvent event = new WarrantyEvent(warrantyId, userId, eventType, claimId, Instant.now());
        kafkaTemplate.send(WARRANTY_EVENTS_TOPIC, warrantyId.toString(), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish WarrantyEvent [type={}, warrantyId={}]",
                                eventType, warrantyId, ex);
                    }
                });
    }
}
