/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Edge layer for the wearables platform.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>JWT validation (via {@link com.wearables.gateway.filter.JwtAuthenticationFilter})</li>
 *   <li>Request routing to downstream microservices</li>
 *   <li>Request/response logging (via {@link com.wearables.gateway.filter.RequestLoggingFilter})</li>
 *   <li>Propagation of {@code X-User-Id} and {@code X-User-Roles} headers downstream</li>
 * </ul>
 *
 * <p>Built on Spring Cloud Gateway (reactive / Netty) — do <em>not</em> mix servlet
 * components (e.g. {@code @Controller}, {@code Filter}) with this application.
 */
@SpringBootApplication
public class ApiGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServiceApplication.class, args);
    }
}
