package com.healthcare.models;

import com.healthcare.models.enums.ConsentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consent_requests", indexes = {
        @Index(name = "idx_prescription_patient", columnList = "patient_id"),
        @Index(name = "idx_prescription_provider", columnList = "provider_id")
})
public class ConsentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<ConsentItem> requestedItems;

    @Enumerated(EnumType.STRING)
    private ConsentStatus status = ConsentStatus.PENDING;

    private String providerReason;

    private String patientComment;

    private LocalDateTime expiryTime;

    private LocalDateTime createdAt = LocalDateTime.now();

}
