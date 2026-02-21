/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.decision.application.service;

import com.wearables.decision.domain.event.AlertEvent;
import com.wearables.decision.domain.event.DeviceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Stateless rule engine that converts a {@link DeviceEvent} into an optional
 * {@link AlertEvent} when a health threshold or safety condition is breached.
 *
 * <p>Rules are evaluated in priority order. The first matching rule wins and
 * produces exactly one alert per event. Events that match no rule return empty.
 */
@Slf4j
@Service
public class RuleEngine {

    // -------------------------------------------------------------------------
    // Thresholds (candidates for externalisation via a config/DB in the future)
    // -------------------------------------------------------------------------
    private static final int HEART_RATE_HIGH_BPM  = 120;
    private static final int HEART_RATE_LOW_BPM   = 45;
    private static final int BATTERY_LOW_PERCENT  = 15;

    /**
     * Evaluate all applicable rules for the given device event.
     *
     * @return an {@link AlertEvent} if any rule fired, or empty.
     */
    public Optional<AlertEvent> evaluate(DeviceEvent event) {
        return switch (event.eventType()) {
            case "HEART_RATE"    -> evaluateHeartRate(event);
            case "FALL_DETECTED" -> evaluateFall(event);
            case "BATTERY_LOW"   -> evaluateBattery(event);
            default -> {
                log.debug("No alert rules for eventType={}", event.eventType());
                yield Optional.empty();
            }
        };
    }

    // -------------------------------------------------------------------------
    // Individual rule evaluators
    // -------------------------------------------------------------------------

    private Optional<AlertEvent> evaluateHeartRate(DeviceEvent event) {
        Number bpm = (Number) event.payload().get("heartRate");
        if (bpm == null) return Optional.empty();

        int heartRate = bpm.intValue();

        if (heartRate > HEART_RATE_HIGH_BPM) {
            return Optional.of(alert(event,
                    "HIGH_HEART_RATE",
                    "High Heart Rate Alert",
                    String.format("Your heart rate is %d bpm — above the %d bpm threshold. "
                            + "Please rest and consult a doctor if it persists.", heartRate, HEART_RATE_HIGH_BPM)));
        }

        if (heartRate < HEART_RATE_LOW_BPM) {
            return Optional.of(alert(event,
                    "LOW_HEART_RATE",
                    "Low Heart Rate Alert",
                    String.format("Your heart rate is %d bpm — below the %d bpm threshold. "
                            + "Seek medical advice if you feel unwell.", heartRate, HEART_RATE_LOW_BPM)));
        }

        return Optional.empty();
    }

    private Optional<AlertEvent> evaluateFall(DeviceEvent event) {
        // Any FALL_DETECTED event triggers an alert regardless of severity
        String severity = (String) event.payload().getOrDefault("severity", "UNKNOWN");
        return Optional.of(alert(event,
                "FALL_DETECTED",
                "Fall Detected",
                String.format("A fall has been detected (severity: %s). "
                        + "Are you okay? Tap to dismiss or call for help.", severity)));
    }

    private Optional<AlertEvent> evaluateBattery(DeviceEvent event) {
        Number pct = (Number) event.payload().get("batteryPercent");
        if (pct == null) return Optional.empty();

        int percent = pct.intValue();
        if (percent <= BATTERY_LOW_PERCENT) {
            return Optional.of(alert(event,
                    "BATTERY_LOW",
                    "Low Battery",
                    String.format("Your device battery is at %d%%. Please charge it soon.", percent)));
        }
        return Optional.empty();
    }

    // -------------------------------------------------------------------------

    private AlertEvent alert(DeviceEvent source, String alertType, String title, String message) {
        log.info("Rule fired: alertType={} for deviceId={} userId={}", alertType, source.deviceId(), source.userId());
        return new AlertEvent(
                UUID.randomUUID(),
                source.deviceId(),
                source.userId(),
                alertType,
                title,
                message,
                Instant.now()
        );
    }
}
