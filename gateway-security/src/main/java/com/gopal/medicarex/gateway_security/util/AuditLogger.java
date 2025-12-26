package com.gopal.medicarex.gateway_security.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
public class AuditLogger {

    public void logSecurityEvent(SecurityEventType eventType, String userId,
                                 String resource, Map<String, Object> metadata) {
        log.info("SECURITY_EVENT | Type: {} | User: {} | Resource: {} | Timestamp: {} | Metadata: {}",
                eventType, userId, resource, Instant.now(), metadata);
    }

    public void logAuthenticationSuccess(String userId, String method) {
        logSecurityEvent(SecurityEventType.AUTH_SUCCESS, userId, method,
                Map.of("method", method, "timestamp", Instant.now()));
    }

    public void logAuthenticationFailure(String userId, String reason) {
        logSecurityEvent(SecurityEventType.AUTH_FAILURE, userId, "authentication",
                Map.of("reason", reason, "timestamp", Instant.now()));
    }

    public void logAccessDenied(String userId, String resource) {
        logSecurityEvent(SecurityEventType.ACCESS_DENIED, userId, resource,
                Map.of("timestamp", Instant.now()));
    }

    public void logRateLimitExceeded(String userId, String endpoint) {
        logSecurityEvent(SecurityEventType.RATE_LIMIT_EXCEEDED, userId, endpoint,
                Map.of("timestamp", Instant.now()));
    }

    public enum SecurityEventType {
        AUTH_SUCCESS,
        AUTH_FAILURE,
        ACCESS_DENIED,
        RATE_LIMIT_EXCEEDED,
        TOKEN_VALIDATION_FAILED,
        CERTIFICATE_VALIDATION_FAILED
    }
}