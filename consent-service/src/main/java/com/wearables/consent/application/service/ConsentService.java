/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.application.service;

import com.wearables.consent.application.dto.ConsentResponse;
import com.wearables.consent.application.dto.UpdateConsentRequest;
import com.wearables.consent.domain.entity.ConsentType;
import com.wearables.consent.domain.entity.UserConsent;
import com.wearables.consent.domain.event.ConsentEvent;
import com.wearables.consent.domain.repository.UserConsentRepository;
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
public class ConsentService {

    static final String CONSENT_EVENTS_TOPIC = "consent.events";

    private final UserConsentRepository consentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ConsentResponse> getAllConsents(UUID userId) {
        return consentRepository.findByUserId(userId).stream()
                .map(ConsentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsentResponse getConsent(UUID userId, ConsentType consentType) {
        return consentRepository.findByUserIdAndConsentType(userId, consentType)
                .map(ConsentResponse::from)
                .orElseThrow(() -> new NoSuchElementException(
                        "No consent record found for type: " + consentType));
    }

    // -------------------------------------------------------------------------
    // Grant / Revoke
    // -------------------------------------------------------------------------

    /**
     * Upsert a consent record — creates it on first use, otherwise updates in-place.
     * Increments {@code version} on every change and records the client IP for audit.
     */
    @Transactional
    public ConsentResponse updateConsent(UUID userId, ConsentType consentType,
                                         UpdateConsentRequest req, String ipAddress) {
        UserConsent consent = consentRepository
                .findByUserIdAndConsentType(userId, consentType)
                .orElseGet(() -> UserConsent.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .consentType(consentType)
                        .version(0)
                        .build());

        boolean nowGranted = req.granted();
        consent.setGranted(nowGranted);
        consent.setVersion(consent.getVersion() + 1);
        consent.setIpAddress(ipAddress);
        consent.setUpdatedAt(Instant.now());

        if (nowGranted) {
            consent.setGrantedAt(Instant.now());
        } else {
            consent.setRevokedAt(Instant.now());
        }

        consent = consentRepository.save(consent);

        String eventType = nowGranted ? "CONSENT_GRANTED" : "CONSENT_REVOKED";
        publishEvent(userId, consentType, eventType, consent.getVersion());
        log.info("Consent updated [userId={}, type={}, granted={}, version={}]",
                userId, consentType, nowGranted, consent.getVersion());

        return ConsentResponse.from(consent);
    }

    // -------------------------------------------------------------------------

    private void publishEvent(UUID userId, ConsentType consentType,
                               String eventType, int version) {
        ConsentEvent event = new ConsentEvent(userId, consentType.name(), eventType,
                version, Instant.now());
        kafkaTemplate.send(CONSENT_EVENTS_TOPIC, userId.toString(), event)
                .whenComplete((r, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ConsentEvent [type={}, userId={}]",
                                eventType, userId, ex);
                    }
                });
    }
}
