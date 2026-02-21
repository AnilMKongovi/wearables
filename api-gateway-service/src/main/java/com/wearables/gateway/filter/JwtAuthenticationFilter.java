/*
 * Copyright (c) 2026 Pranaah Electronics Pvt Ltd. All rights reserved.
 *
 * This source code and all associated files are the proprietary and confidential
 * property of Pranaah Electronics Pvt Ltd. Unauthorized copying, modification,
 * distribution, or use of this software, via any medium, is strictly prohibited
 * without the express written permission of Pranaah Electronics Pvt Ltd.
 */

package com.wearables.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Reactive {@link GlobalFilter} that validates the JWT Bearer token on every
 * inbound request before it is forwarded to a downstream service.
 *
 * <p>Public paths (login, refresh, logout, actuator) bypass validation.
 * For authenticated requests the filter injects {@code X-User-Id} and
 * {@code X-User-Roles} headers so downstream services can trust the caller's
 * identity without re-parsing the JWT.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    /** Paths that do not require a JWT. */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/actuator"
    );

    /**
     * Internal service paths that should never be reachable via the gateway —
     * reject them with 403 even if a valid JWT is present.
     */
    private static final List<String> BLOCKED_PATHS = List.of(
            "/api/v1/auth/internal/",
            "/api/v1/devices/internal/"
    );

    private final SecretKey signingKey;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Block internal-only endpoints from external access
        if (BLOCKED_PATHS.stream().anyMatch(path::startsWith)) {
            log.warn("Blocked access to internal path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // Public paths pass through without JWT validation
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Attach resolved identity headers for downstream services
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Id",    claims.getSubject())
                    .header("X-User-Roles", (String) claims.getOrDefault("roles", ""))
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (JwtException ex) {
            log.debug("JWT validation failed for path {}: {}", path, ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /** Runs before all other filters (e.g. routing). */
    @Override
    public int getOrder() {
        return -100;
    }
}
