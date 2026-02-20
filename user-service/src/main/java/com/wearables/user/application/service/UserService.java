/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.user.application.service;

import com.wearables.user.application.dto.CreateUserRequest;
import com.wearables.user.application.dto.UpdatePreferencesRequest;
import com.wearables.user.application.dto.UpdateProfileRequest;
import com.wearables.user.application.dto.UserResponse;
import com.wearables.user.domain.entity.User;
import com.wearables.user.domain.entity.UserPreferences;
import com.wearables.user.domain.entity.UserProfile;
import com.wearables.user.domain.event.UserCreatedEvent;
import com.wearables.user.domain.repository.UserPreferencesRepository;
import com.wearables.user.domain.repository.UserProfileRepository;
import com.wearables.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String TOPIC_USER_CREATED = "user.created";

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserPreferencesRepository preferencesRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Registers a new user and creates default profile + preference rows.
     * Publishes a {@code user.created} Kafka event on success.
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        if (request.getPhone() != null && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone already registered: " + request.getPhone());
        }

        Instant now = Instant.now();
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email(request.getEmail())
                .phone(request.getPhone())
                .countryCode(request.getCountryCode())
                .status(User.Status.ACTIVE)
                .emailVerified(false)
                .phoneVerified(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
        userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
        profileRepository.save(profile);

        UserPreferences prefs = UserPreferences.builder()
                .userId(userId)
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();
        preferencesRepository.save(prefs);

        kafkaTemplate.send(TOPIC_USER_CREATED, userId.toString(),
                new UserCreatedEvent(userId, user.getEmail(), user.getPhone(), user.getCountryCode(), now));

        log.info("User created: {}", userId);
        return UserResponse.from(user, profile, prefs);
    }

    /** Returns the full user response (user + profile + preferences) by ID. */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = findActiveOrThrow(userId);
        UserProfile profile = profileRepository.findByUserId(userId).orElse(null);
        UserPreferences prefs = preferencesRepository.findByUserId(userId).orElse(null);
        return UserResponse.from(user, profile, prefs);
    }

    /** Returns the full user response by email address. */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
        UserProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        UserPreferences prefs = preferencesRepository.findByUserId(user.getId()).orElse(null);
        return UserResponse.from(user, profile, prefs);
    }

    /** Updates mutable profile fields; returns the updated full response. */
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findActiveOrThrow(userId);

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    return profileRepository.save(UserProfile.builder()
                            .userId(userId).user(user).createdAt(now).updatedAt(now).build());
                });

        if (request.getFirstName() != null)   profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null)    profile.setLastName(request.getLastName());
        if (request.getDisplayName() != null) profile.setDisplayName(request.getDisplayName());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null)      profile.setGender(request.getGender());
        if (request.getAvatarUrl() != null)   profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getBio() != null)         profile.setBio(request.getBio());
        profile.setUpdatedAt(Instant.now());
        profileRepository.save(profile);

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        UserPreferences prefs = preferencesRepository.findByUserId(userId).orElse(null);
        return UserResponse.from(user, profile, prefs);
    }

    /** Updates notification and display preferences; returns the updated full response. */
    @Transactional
    public UserResponse updatePreferences(UUID userId, UpdatePreferencesRequest request) {
        User user = findActiveOrThrow(userId);

        UserPreferences prefs = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    return preferencesRepository.save(UserPreferences.builder()
                            .userId(userId).user(user).createdAt(now).updatedAt(now).build());
                });

        if (request.getLanguage() != null)                  prefs.setLanguage(request.getLanguage());
        if (request.getTimezone() != null)                  prefs.setTimezone(request.getTimezone());
        if (request.getNotificationsEnabled() != null)      prefs.setNotificationsEnabled(request.getNotificationsEnabled());
        if (request.getEmailNotificationsEnabled() != null) prefs.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        if (request.getSmsNotificationsEnabled() != null)   prefs.setSmsNotificationsEnabled(request.getSmsNotificationsEnabled());
        if (request.getPushNotificationsEnabled() != null)  prefs.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
        prefs.setUpdatedAt(Instant.now());
        preferencesRepository.save(prefs);

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        UserProfile profile = profileRepository.findByUserId(userId).orElse(null);
        return UserResponse.from(user, profile, prefs);
    }

    /**
     * Soft-deletes a user by setting status to DELETED.
     * Does not remove underlying rows — profile and preferences are retained.
     */
    @Transactional
    public void deactivateUser(UUID userId) {
        User user = findActiveOrThrow(userId);
        user.setStatus(User.Status.DELETED);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("User deactivated: {}", userId);
    }

    private User findActiveOrThrow(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        if (user.getStatus() == User.Status.DELETED) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return user;
    }
}
