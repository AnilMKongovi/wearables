/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.messaging;

import com.wearables.consent.domain.entity.ConsentType;
import com.wearables.consent.domain.entity.NotificationPreference;
import com.wearables.consent.domain.entity.UserConsent;
import com.wearables.consent.domain.event.UserCreatedEvent;
import com.wearables.consent.domain.repository.NotificationPreferenceRepository;
import com.wearables.consent.domain.repository.UserConsentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Consumes {@code user.created} events published by user-service and eagerly
 * seeds per-user rows in consent-service with safe defaults.
 *
 * <p>Seeding strategy:
 * <ul>
 *   <li>One {@link UserConsent} row per {@link ConsentType}, all set to {@code granted=false}.
 *       The user must explicitly opt in through the consent API.</li>
 *   <li>One {@link NotificationPreference} row with push enabled, DND and marketing
 *       disabled, and health alerts / reminders enabled.</li>
 * </ul>
 *
 * <p>The handler is idempotent: if rows already exist (e.g. duplicate delivery),
 * they are left unchanged.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventConsumer {

    static final String USER_CREATED_TOPIC = "user.created";

    private final UserConsentRepository consentRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    @KafkaListener(topics = USER_CREATED_TOPIC, groupId = "consent-service")
    @Transactional
    public void onUserCreated(UserCreatedEvent event) {
        UUID userId = event.userId();
        log.info("Received user.created event [userId={}, countryCode={}]", userId, event.countryCode());

        seedConsents(userId);
        seedNotificationPreference(userId);

        log.info("Consent and preference records seeded [userId={}]", userId);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Creates one {@link UserConsent} row per {@link ConsentType}, all with
     * {@code granted=false}. Skips types for which a row already exists.
     */
    private void seedConsents(UUID userId) {
        for (ConsentType type : ConsentType.values()) {
            boolean exists = consentRepository
                    .findByUserIdAndConsentType(userId, type)
                    .isPresent();
            if (exists) {
                log.debug("Consent row already exists, skipping [userId={}, type={}]", userId, type);
                continue;
            }
            consentRepository.save(UserConsent.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .consentType(type)
                    .granted(false)
                    .version(0)
                    .updatedAt(Instant.now())
                    .build());
            log.debug("Seeded consent record [userId={}, type={}, granted=false]", userId, type);
        }
    }

    /**
     * Creates a {@link NotificationPreference} row with safe defaults.
     * No-ops if a row already exists.
     */
    private void seedNotificationPreference(UUID userId) {
        if (preferenceRepository.findByUserId(userId).isPresent()) {
            log.debug("Notification preference already exists, skipping [userId={}]", userId);
            return;
        }
        preferenceRepository.save(NotificationPreference.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pushEnabled(true)
                .quietHoursEnabled(false)
                .quietHoursStart("22:00")
                .quietHoursEnd("07:00")
                .dndEnabled(false)
                .alertsEnabled(true)
                .remindersEnabled(true)
                .marketingEnabled(false)
                .updatedAt(Instant.now())
                .build());
        log.debug("Seeded notification preference record [userId={}, push=true, dnd=false]", userId);
    }
}
