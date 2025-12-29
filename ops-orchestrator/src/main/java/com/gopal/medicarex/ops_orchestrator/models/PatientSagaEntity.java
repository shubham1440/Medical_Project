package com.gopal.medicarex.ops_orchestrator.models;

import com.gopal.medicarex.ops_orchestrator.models.enums.PatientState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_sagas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSagaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sagaId;

    // Business Keys
    @Column(nullable = false, unique = true)
    private String patientId;

    private String bedId;

    // The current progress in the workflow
    @Enumerated(EnumType.STRING)
    private PatientState currentState;

    // Data gathered during the saga
    private Double totalBillAmount;
    private String paymentReference;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @CreationTimestamp
    private LocalDateTime startTime;

    @UpdateTimestamp
    private LocalDateTime lastUpdateTime;
}
