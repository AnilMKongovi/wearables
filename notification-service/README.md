Notification Service — CORE ENGINE

This service talks to OneSignal.

Responsibilities
	Build notification payload
	Call OneSignal REST API
	Retry on failure
	Template management

Internal modules
	Notification composer
	Provider adapter (OneSignal)
	Retry handler
	Dead-letter handling

Tech
	Spring Boot
	WebClient / RestTemplate
	Resilience4j (important)


notification-service (port 8086)
Kafka consumer on alert.events
Calls device-registry internal endpoint to resolve FCM/APNs push tokens
Dispatches push via OneSignal REST API with Resilience4j @Retry (3 attempts, 2s backoff) + @CircuitBreaker (50% failure rate threshold); fallback logs for DLQ
