/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.ingestion.dto;

/**
 * Supported event types emitted by wearable devices / mobile app sync / BLE gateway.
 */
public enum EventType {
    /** Abnormal heart rate reading (high or low). Payload: {@code { "heartRate": <bpm> }}. */
    HEART_RATE,

    /** Device has collected enough sleep data for analysis. Payload: {@code { "durationMinutes": <n> }}. */
    SLEEP_READY,

    /** Sudden fall detected by accelerometer/gyroscope. Payload: {@code { "severity": "LOW|MED|HIGH" }}. */
    FALL_DETECTED,

    /** Battery charge is low. Payload: {@code { "batteryPercent": <0-100> }}. */
    BATTERY_LOW,

    /** Periodic blood-oxygen reading. Payload: {@code { "spo2Percent": <0-100> }}. */
    SPO2,

    /** General telemetry / health check ping from device. Payload: arbitrary. */
    TELEMETRY
}
