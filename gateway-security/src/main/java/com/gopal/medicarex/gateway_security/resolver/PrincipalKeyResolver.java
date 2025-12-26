package com.gopal.medicarex.gateway_security.resolver;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class PrincipalKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(auth -> {
                    if (auth instanceof JwtAuthenticationToken jwtAuth) {
                        Jwt jwt = jwtAuth.getToken();

                        // Check if this is a partner request
                        String clientId = jwt.getClaimAsString("azp"); // Authorized party
                        if (clientId != null && clientId.startsWith("partner-")) {
                            return "partner:" + clientId;
                        }

                        // Extract user ID for staff/internal users
                        String userId = jwt.getSubject();
                        String department = jwt.getClaimAsString("department");

                        return String.format("user:%s:dept:%s", userId, department != null ? department : "general");
                    }

                    // Fallback to principal name
                    return auth.getName();
                })
                .switchIfEmpty(Mono.just("anonymous"));
    }
}


