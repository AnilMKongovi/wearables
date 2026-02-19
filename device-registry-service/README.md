# device-registry-service

Spring Boot microservice for the wearable platform.

## Build
mvn clean package

## Run
java -jar target/device-registry-service.jar

Device Registry Service — CRITICAL for push

This is the most important service for notifications.

Purpose: User ↔ Device ↔ OneSignal Player ID

Metadata: user_id, device_id, subscription_id (ex: onesignal_player_id), platform (Android, iOS, Web)

APIs
	register device
	update token
	deactivate device 
