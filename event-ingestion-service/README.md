Event Ingestion Service — VERY IMPORTANT for wearables

Smart wearable ecosystem will generate events like:
	heart rate abnormal
	sleep ready
	fall detected
	battery low

Purpose
	Accept high-volume telemetry.

Input sources
	mobile app sync
	BLE gateway
	device cloud

event-ingestion-service (port 8084)

POST /api/v1/events — accepts telemetry from devices/mobile/BLE gateway with JWT auth
userId is always extracted from the JWT subject (never trusted from the request body)
Publishes DeviceEvent to the device.events Kafka topic
EventType enum: HEART_RATE, SLEEP_READY, FALL_DETECTED, BATTERY_LOW, SPO2, TELEMETRY
