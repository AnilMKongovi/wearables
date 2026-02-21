# api-gateway-service

Spring Boot microservice for the wearable platform.

## Build
mvn clean package

## Run
java -jar target/api-gateway-service.jar

API Gateway (Edge layer) — REQUIRED

Purpose
	Single entry point
	Auth enforcement
	Rate limiting
	Routing

Recommended stack
	Spring Cloud Gateway
	Kong (optional)
	NGINX (infra level)

Responsibilities
	JWT validation
	Request routing
	Throttling
	Logging

api-gateway-service (port 8080)
Spring Cloud Gateway (reactive/Netty) replacing the servlet web stub
JwtAuthenticationFilter (order -100): validates JWT, attaches X-User-Id + X-User-Roles headers, blocks /internal/** from external access
RequestLoggingFilter (order -99): logs method, path, status, latency
Routes: auth-service, user-service, device-registry, event-ingestion
Absolutely mandatory in microservices.


