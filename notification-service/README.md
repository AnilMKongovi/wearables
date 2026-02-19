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
