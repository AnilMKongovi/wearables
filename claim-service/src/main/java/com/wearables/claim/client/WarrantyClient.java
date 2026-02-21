/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.claim.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * HTTP client for warranty-service. Forwards the caller's JWT so that
 * warranty-service can enforce its own authentication.
 */
@Component
public class WarrantyClient {

    private final RestClient restClient;

    public WarrantyClient(@Value("${warranty.service.url}") String warrantyServiceUrl) {
        this.restClient = RestClient.create(warrantyServiceUrl);
    }

    /**
     * Fetch warranty info by ID from warranty-service.
     *
     * @param warrantyId  the warranty UUID to look up
     * @param authHeader  the {@code Authorization: Bearer <token>} header to forward
     * @return            a lightweight {@link WarrantyInfo} with the fields needed for claim validation
     * @throws NoSuchElementException if warranty-service returns 404
     */
    public WarrantyInfo getWarranty(UUID warrantyId, String authHeader) {
        try {
            return restClient.get()
                    .uri("/api/v1/warranties/{id}", warrantyId)
                    .header("Authorization", authHeader)
                    .retrieve()
                    .body(WarrantyInfo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NoSuchElementException("Warranty not found: " + warrantyId);
        }
    }
}
