# user-service

Spring Boot microservice for the wearable platform.

## Build
mvn clean package

## Run
java -jar target/user-service.jar


Summary: user-service 

Domain:
- User entity with indexes on email and phone
- UserProfile (shared PK one-to-one: firstName, lastName, displayName, dateOfBirth, gender, avatarUrl, bio)
- UserPreferences (shared PK one-to-one: language, timezone, push/email/ SMS notification toggles with sensible defaults)
- UserDevices (lightweight device reference; device-registry-service is source of truth)

Repositories:
- UserRepository, UserProfileRepository, UserPreferencesRepository, UserDevicesRepository — all with correct com.wearables.user package

Application layer:
- UserService: createUser (idempotent on email/phone), getUserById, getUserByEmail, updateProfile, updatePreferences, deactivateUser (soft delete); publishes UserCreatedEvent to user.created Kafka topic
- UserResponse DTO with embedded profile + preferences
- CreateUserRequest, UpdateProfileRequest, UpdatePreferencesRequest DTOs

API (REST):
- POST   /api/v1/users          — register user (permit-all, called by auth-service)
- GET    /api/v1/users/me       — current user from JWT subject
- GET    /api/v1/users/{id}     — fetch by ID
- PUT    /api/v1/users/{id}/profile      — update profile fields
- PUT    /api/v1/users/{id}/preferences  — update notification prefs
- DELETE /api/v1/users/{id}     — soft-delete (sets status=DELETED)

Security:
- JwtAuthenticationFilter — validates HMAC-signed Bearer token (same signing key as auth-service / device-registry-service)
- SecurityConfig — stateless, CSRF disabled; POST /api/v1/users and /actuator/** are public; everything else requires auth

Config:
- KafkaConfig: producer with JSON serializer, acks=all, retries=3
- RedisConfig: RedisTemplate with StringSerializer keys + JSON values
- WebConfig: CORS mapping for /api/**

Infrastructure:
- AuthServiceClient: HTTP client to revoke user sessions on deactivation
- UserCreatedEvent: Kafka record type for user.created topic
- application.yaml: port 8080, DB/Kafka/Redis/JWT env-var overrides
