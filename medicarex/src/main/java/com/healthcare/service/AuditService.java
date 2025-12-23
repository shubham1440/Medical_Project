package com.healthcare.service;

import com.healthcare.dto.response.AuditLogResponse;

import java.util.List;
import java.util.Map;

public interface AuditService {
    void logAction(String createProvider, String provider, Long id, String s);

    public List<AuditLogResponse> getAuditLogs(String actorEmail, String action, String from, String to);

    void recordEvent(
            String actorUserId,
            String action,
            String entityType,
            String entityId,
            String correlationId,
            Map<String, Object> meta
    );
}
