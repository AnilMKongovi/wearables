# wearables

Smart Wearables Micro-services

Refer "architecture" for design diagrams

Smart Wearable ecosystem:
	Push infra: Firebase FCM - FCM handles device delivery reliably
	Engagement layer: OneSignal - OneSignal gives marketing + segmentation

Data Flow:
	Upstream: Mobile / Wearable → API Gateway  → Core Microservices → Event Bus / Queue (future requirement) → Notification Service → OneSignal → FCM/APNs
	Downstream: Wearables Backend → OneSignal REST API → FCM/APNs → Device

Advantages:
	Easy to scale later
	Low initial cost

Phase 1 (must have)
	API Gateway
	User Service
	Device Registry Service
	Notification Service
	PostgreSQL or NoSQL
	OneSignal integration

Phase 2 (wearable intelligence)
	Event Ingestion
	Kafka
	Rules Engine
	Scheduler

Phase 3 (scale & enterprise)
	Preference service
	Observability stack
	Redis caching
	advanced segmentation
