package com.gopal.medicarex.gateway_security.util;

import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class JwtUtils {

    public static String extractUserId(Jwt jwt) {
        return jwt.getSubject();
    }

    public static String extractEmail(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    public static String extractPreferredUsername(Jwt jwt) {
        return jwt.getClaimAsString("preferred_username");
    }

    public static String extractDepartment(Jwt jwt) {
        return jwt.getClaimAsString("department");
    }

    @SuppressWarnings("unchecked")
    public static List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            return (List<String>) realmAccess.get("roles");
        }
        return List.of();
    }

    public static boolean isTokenExpired(Jwt jwt) {
        Instant expiresAt = jwt.getExpiresAt();
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public static boolean hasScope(Jwt jwt, String scope) {
        String scopeString = jwt.getClaimAsString("scope");
        return scopeString != null && scopeString.contains(scope);
    }
}

