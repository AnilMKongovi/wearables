# consent-service

Spring Boot microservice for the wearable platform.

## Build
mvn clean package

## Run
java -jar target/consent-service.jar


Summary:consent-service 

Preference & Consent Service — IMPORTANT: India compliance - DPDP - Digital Personal Data Protection (DPDP) Act, 2023



Store:
push opt-in
quiet hours
DND settings
regulatory consent

This prevents spam complaints and improves delivery.


consent-service (port 8089, DB: consent_service)
Domain
ConsentType — five consent categories:

HEALTH_DATA_COLLECTION, DATA_SHARING_THIRD_PARTY, MARKETING_COMMUNICATIONS, RESEARCH_ANALYTICS, REGULATORY_INDIA_DPDP
UserConsent — one row per (userId, consentType); tracks granted, version (monotonically incrementing), grantedAt, revokedAt, ipAddress (for DPDP Act audit trail)

NotificationPreference — one row per user; push opt-in, quiet hours (HH:mm), DND, per-type toggles (alerts, reminders, marketing)

API
Method	Path	Description
GET	/api/v1/consents	List all consent records for the user
GET	/api/v1/consents/{consentType}	Get specific consent
PUT	/api/v1/consents/{consentType}	Grant or revoke ({"granted": true/false})
GET	/api/v1/preferences/notifications	Get prefs (returns defaults if none saved)
PUT	/api/v1/preferences/notifications	Upsert push/quiet-hours/DND/type prefs


Key design points
Upsert pattern — no separate "create" endpoint; first PUT creates the row
IP capture — resolved server-side from X-Forwarded-For or REMOTE_ADDR, not from the request body
Kafka — publishes CONSENT_GRANTED / CONSENT_REVOKED to consent.events topic so notification-service and decision-service can react
Stale scaffolding replaced — removed com.yourorg package, jjwt 0.11.5, Spring Boot 3.2 parent; now inherits from the platform root BOM
Gateway routes — added claim-service, consent-service-consents, and consent-service-preferences routes to api-gateway-service
The consent-service implementation is complete and pushed. The background exploration agent finished and confirmed the implementation aligns with all platform patterns — correct groupId, Spring Boot version, JWT library version (0.12.5), Kafka producer setup, and ErrorResponse usage from common-libraries.

user-service publishes a user.created Kafka event when a new user registers and consent-service auto-initialize consent rows (e.g., pre-set REGULATORY_INDIA_DPDP to false) when a user signs up.
