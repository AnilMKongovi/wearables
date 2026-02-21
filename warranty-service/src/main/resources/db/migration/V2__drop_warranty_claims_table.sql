-- Claims are now managed by the dedicated claim-service with its own database.
-- Remove the warranty_claims table from the warranty-service schema.
DROP TABLE IF EXISTS warranty_claims;
