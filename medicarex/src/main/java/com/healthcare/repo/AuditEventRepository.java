package com.healthcare.repo;

import com.healthcare.models.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Page<AuditEvent> findByActorId(Long actorId, Pageable pageable);

    Page<AuditEvent> findByAction(String action, Pageable pageable);

    Page<AuditEvent> findByEntityTypeAndEntityId(
            String entityType,
            Long entityId,
            Pageable pageable
    );

    @Query("SELECT ae FROM AuditEvent ae WHERE ae.timestamp BETWEEN :start AND :end")
    Page<AuditEvent> findByTimestampBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}

