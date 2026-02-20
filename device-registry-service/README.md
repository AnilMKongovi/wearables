# device-registry-service

Spring Boot microservice for the wearable platform.

## Build
mvn clean package

## Run
java -jar target/device-registry-service.jar


## Summary
**device-registry-service**, a new microservice responsible for managing device registrations, push subscription tokens, and device lifecycle across the Pranaah wearables platform. The service enables multi-device support (phones, tablets, smart rings, smart glasses) and integrates with JWT-based authentication from the auth-service.

## Key Changes

### Core Domain Model
- **Device entity** with support for multiple device types (ANDROID, IOS, WEB, SMART_RING, SMART_GLASSES)
- Device lifecycle management via `DeviceStatus` enum (ACTIVE/INACTIVE)
- Metadata tracking: app version, OS version, registration/update/last-seen timestamps
- Database indexes on `userId` and `subscriptionId` for efficient queries

### Application Layer
- **DeviceService** with transactional operations:
  - `registerDevice()` — idempotent registration (updates existing device by `deviceId` to handle app reinstalls)
  - `updateToken()` — refresh push subscription tokens when FCM/APNs rotates them
  - `deactivateDevice()` — mark device inactive on logout or opt-out
  - `getActiveDevicesForUser()` — retrieve all active devices for a user (used by notification-service)
- **DeviceRepository** with custom queries for user/status/device-type filtering

### REST API
- `POST /api/v1/devices` — Register or re-register a device (201 Created)
- `PUT /api/v1/devices/{deviceId}/token` — Update push subscription token (200 OK)
- `DELETE /api/v1/devices/{deviceId}` — Deactivate a device (204 No Content)
- `GET /api/v1/devices/user/{userId}` — List active devices for a user (200 OK)

### Security
- **JwtAuthenticationFilter** — Validates Bearer tokens issued by auth-service
- Extracts user identity and roles from JWT claims
- Populates Spring Security context for downstream authorization
- **SecurityConfig** — Stateless session policy, CSRF disabled, all endpoints require authentication except `/actuator/**`

### Configuration & Dependencies
- Updated `pom.xml`:
  - Added Spring Security, Spring Data JPA, validation, and PostgreSQL driver
  - Integrated JJWT (0.12.5) for JWT parsing
  - Configured Lombok with build plugin exclusion
  - Set Java 21 as target version
- **application.yaml** with PostgreSQL datasource, JPA/Hibernate settings, JWT secret configuration, and actuator endpoints

## Notable Implementation Details
- **Idempotent registration**: Devices are keyed by hardware `deviceId` to prevent duplicates on app reinstall
- **Wearable support**: Smart rings and glasses can omit `subscriptionId` as they inherit push via paired mobile devices
- **Transactional consistency**: All state-changing operations use `@Transactional` to ensure atomicity
- **Timestamp tracking**: Maintains `registeredAt` (immutable), `updatedAt`, and `lastSeenAt` for device lifecycle visibility
- **Comprehensive validation**: Request DTOs use Jakarta validation annotations for input sanitization
