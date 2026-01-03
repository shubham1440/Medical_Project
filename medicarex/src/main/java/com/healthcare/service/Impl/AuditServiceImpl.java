package com.healthcare.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.dto.response.AuditLogResponse;
import com.healthcare.models.AuditEvent;
import com.healthcare.repo.AuditEventRepository;
import com.healthcare.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements com.healthcare.service.AuditService {

    private final AuditEventRepository auditEventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String action, String actor, Long entityId, String details) {
        try {
            AuditEvent auditEvent = AuditEvent.builder()
                    .action(action)
                    .actorId(getActorIdFromActor(actor))
                    .entityType(getEntityTypeFromAction(action))
                    .entityId(entityId)
                    .timestamp(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .requestContext(details)
                    .redactedPayload(null)
                    .ipAddress(getClientIpAddress())
                    .userAgent(getUserAgent())
                    .build();

            auditEventRepository.save(auditEvent);

        } catch (Exception e) {
            log.error("Failed to log audit event: {} by {} for entity {}", action, actor, entityId, e);
        }
    }

    @Override
    public List<AuditLogResponse> getAuditLogs(String actorEmail, String action, String from, String to) {
        List<AuditEvent> events = auditEventRepository.findAll();

        return events.stream()
                .filter(ev ->
                        (actorEmail == null || getActorEmail(ev.getActorId()).equalsIgnoreCase(actorEmail)) &&
                                (action == null || ev.getAction().equalsIgnoreCase(action)) &&
                                (from == null || !ev.getTimestamp().isBefore(LocalDateTime.parse(from))) &&
                                (to == null || !ev.getTimestamp().isAfter(LocalDateTime.parse(to)))
                )
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void recordEvent(
            String actorUserId,
            String action,
            String entityType,
            String entityId,
            String correlationId,
            Map<String, Object> meta) {

        Long actorIdNum = null;
        try {
            actorIdNum = actorUserId != null ? Long.valueOf(actorUserId) : null;
        } catch (NumberFormatException e) {
            log.warn("Invalid actorUserId for audit log: {}", actorUserId);
        }

        Long entityIdNum = null;
        try {
            entityIdNum = entityId != null ? Long.valueOf(entityId) : null;
        } catch (NumberFormatException e) {
            log.debug("EntityId is not a number, storing as null (entityId={})", entityId);
        }

        AuditEvent auditEvent = AuditEvent.builder()
                .actorId(actorIdNum)
                .action(action)
                .entityType(entityType)
                .entityId(entityIdNum)
                .timestamp(LocalDateTime.now())
                .correlationId(correlationId)
                .requestContext(null)
                .redactedPayload(safeSerializeMeta(meta))
                .ipAddress(null)
                .userAgent(null)
                .build();

        auditEventRepository.save(auditEvent);

        log.trace("Audit event recorded: action={}, entityType={}, entityId={}, actorId={}, correlationId={}",
                action, entityType, entityIdNum, actorIdNum, correlationId);
    }

    private Long getActorIdFromActor(String actor) {
        try { return Long.valueOf(actor); } catch (Exception e) { return -1L; }
    }

    private String getEntityTypeFromAction(String action) {
        if (action != null && action.toLowerCase().contains("provider")) {
            return "Provider";
        } else if (action != null && action.toLowerCase().contains("appointment")) {
            return "Appointment";
        }
        else if (action != null && action.toLowerCase().contains("approve_consent_Request")) {
            return "Consent_Request_Approved";
        }
        return "Unknown";
    }

    private String getClientIpAddress() {
        return "";
    }

    private String getUserAgent() {
        return "";
    }

    private AuditLogResponse mapToResponseDto(AuditEvent event) {
        String actorEmail = getActorEmail(event.getActorId());
        String target = event.getEntityType() + (event.getEntityId() != null ? ":" + event.getEntityId() : "");
        String description = event.getRequestContext();

        return AuditLogResponse.builder()
                .id(event.getId())
                .actor(actorEmail)
                .action(event.getAction())
                .target(target)
                .description(description)
                .timestamp(event.getTimestamp())
                .ipAddress(event.getIpAddress())
                .build();
    }

    private String getActorEmail(Long actorId) {
        return userRepository.findById(actorId)
                .map(user -> user.getEmail())
                .orElse("user:" + actorId);
    }

    private String safeSerializeMeta(Map<String, Object> meta) {
        if (meta == null || meta.isEmpty()) return "{}";
        try {
            return new ObjectMapper().writeValueAsString(meta);
        } catch (Exception e) {
            log.warn("Failed to serialize audit meta (ignored)", e);
            return "{}";
        }
    }

}

