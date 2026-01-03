package com.healthcare.models;

import com.healthcare.models.enums.ConsentStatus;
import com.healthcare.models.enums.PermissionType;
import com.healthcare.models.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consents", indexes = {
        @Index(name = "idx_consent_patient", columnList = "patient_id"),
        @Index(name = "idx_consent_provider", columnList = "provider_id"),
        @Index(name = "idx_consent_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "granted_role", length = 20)
    private Role grantedRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type", nullable = false, length = 20)
    private PermissionType permissionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consentRequest_id")
    private ConsentRequest consentRequest;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

//    @OneToOne (fetch = FetchType.LAZY)
//    @JoinColumn(name = "document_Id",nullable = true)
//    private ClinicalDocument clinicalDocument_Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_status", columnDefinition = "VARCHAR(255)")
    private ConsentStatus consentStatus;

    @Column(name = "notes", length = 1000)
    private String notes;
}

