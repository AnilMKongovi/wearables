/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.ingestion.api.controller;

import com.wearables.ingestion.application.service.EventIngestionService;
import com.wearables.ingestion.dto.IngestEventRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventIngestionService ingestionService;

    /**
     * POST /api/v1/events
     *
     * <p>Accept a telemetry event from a wearable device, mobile app, or BLE gateway.
     * The caller must present a valid JWT; the {@code userId} is derived from the
     * token subject rather than the request body to prevent spoofing.
     *
     * @return 202 Accepted — the event has been queued for async processing.
     */
    @PostMapping
    public ResponseEntity<Void> ingestEvent(@Valid @RequestBody IngestEventRequest request,
                                            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ingestionService.ingest(request, userId);
        return ResponseEntity.accepted().build();
    }
}
