/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("oneSignalWebClient")
    public WebClient oneSignalWebClient(
            @Value("${onesignal.api-url}") String apiUrl,
            @Value("${onesignal.api-key}") String apiKey) {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Basic " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean("deviceRegistryWebClient")
    public WebClient deviceRegistryWebClient(
            @Value("${clients.device-registry.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
