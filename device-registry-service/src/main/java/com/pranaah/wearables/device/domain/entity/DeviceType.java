/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.pranaah.wearables.device.domain.entity;

/**
 * Represents the type of device that can be registered in the platform.
 *
 * <p>Mobile platforms (ANDROID, IOS) and browsers (WEB) are traditional push
 * targets via FCM/APNs. SMART_RING and SMART_GLASSES are first-party wearable
 * form factors that pair with a mobile app and inherit its push subscription.
 */
public enum DeviceType {

    /** Android smartphone or tablet. Push via Firebase Cloud Messaging. */
    ANDROID,

    /** Apple iPhone or iPad. Push via APNs through OneSignal. */
    IOS,

    /** Browser-based push subscription (Web Push / VAPID). */
    WEB,

    /** Smart ring wearable (e.g. Pranaah Ring). Paired to a mobile device. */
    SMART_RING,

    /** Smart glasses wearable (e.g. Pranaah Glass). Paired to a mobile device. */
    SMART_GLASSES
}
