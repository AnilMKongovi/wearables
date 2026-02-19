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

Absolutely mandatory in microservices.


