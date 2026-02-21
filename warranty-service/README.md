# warranty-service

Spring Boot microservice for the wearable platform.

## Build
mvn clean package

## Run
java -jar target/warranty-service.jar


Summary: warranty-service 

WarrantyClient 
— calls GET /api/v1/warranties/{id} on warranty-service (forwarding the caller's JWT) to validate warranty ownership and ACTIVE status before a claim is opened
- Configured via warranty.service.url env var (default http://localhost:8087)
