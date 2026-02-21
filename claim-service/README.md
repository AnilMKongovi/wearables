# claim-service

Spring Boot microservice for the wearable platform.

## Build
mvn clean package

## Run
java -jar target/claim-service.jar


Summary: claim-service 

New claim-service (port 8088, DB: claim_service)

ClaimService — handles the claim lifecycle (open, query, update); no longer coupled to WarrantyService in-process
ClaimEvent — publishes CLAIM_OPENED / CLAIM_RESOLVED events to the shared warranty.events Kafka topic (same JSON structure as before, backward-compatible with consumers)
Own DB schema — V1__create_claim_tables.sql creates warranty_claims with no FK constraint to warranty_registrations (cross-service FKs are an anti-pattern in microservices)
