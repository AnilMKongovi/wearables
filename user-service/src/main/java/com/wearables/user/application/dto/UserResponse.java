/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.user.application.dto;

import com.wearables.user.domain.entity.User;
import com.wearables.user.domain.entity.UserPreferences;
import com.wearables.user.domain.entity.UserProfile;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String phone;
    private String countryCode;
    private User.Status status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    // Embedded profile
    private String firstName;
    private String lastName;
    private String displayName;
    private LocalDate dateOfBirth;
    private String gender;
    private String avatarUrl;
    private String bio;

    // Embedded preferences
    private String language;
    private String timezone;
    private Boolean notificationsEnabled;
    private Boolean emailNotificationsEnabled;
    private Boolean smsNotificationsEnabled;
    private Boolean pushNotificationsEnabled;

    public static UserResponse from(User user, UserProfile profile, UserPreferences prefs) {
        UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .countryCode(user.getCountryCode())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt());

        if (profile != null) {
            builder.firstName(profile.getFirstName())
                   .lastName(profile.getLastName())
                   .displayName(profile.getDisplayName())
                   .dateOfBirth(profile.getDateOfBirth())
                   .gender(profile.getGender())
                   .avatarUrl(profile.getAvatarUrl())
                   .bio(profile.getBio());
        }

        if (prefs != null) {
            builder.language(prefs.getLanguage())
                   .timezone(prefs.getTimezone())
                   .notificationsEnabled(prefs.getNotificationsEnabled())
                   .emailNotificationsEnabled(prefs.getEmailNotificationsEnabled())
                   .smsNotificationsEnabled(prefs.getSmsNotificationsEnabled())
                   .pushNotificationsEnabled(prefs.getPushNotificationsEnabled());
        }

        return builder.build();
    }
}
