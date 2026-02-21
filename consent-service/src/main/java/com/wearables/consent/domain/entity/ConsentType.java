/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.consent.domain.entity;

/**
 * Consent categories tracked by the platform.
 *
 * <p>REGULATORY_INDIA_DPDP must be granted before the platform may process
 * any personal data for users in India (DPDP Act 2023).
 */
public enum ConsentType {

    /** Consent to collect health, activity, and biometric data from paired devices. */
    HEALTH_DATA_COLLECTION,

    /** Consent to share personal/health data with authorised third parties. */
    DATA_SHARING_THIRD_PARTY,

    /** Consent to receive marketing and promotional communications. */
    MARKETING_COMMUNICATIONS,

    /** Consent to use anonymised data for research and analytics. */
    RESEARCH_ANALYTICS,

    /**
     * Regulatory consent required under the Digital Personal Data Protection
     * Act, 2023 (India). Must be GRANTED before data collection may begin for
     * Indian residents.
     */
    REGULATORY_INDIA_DPDP
}
