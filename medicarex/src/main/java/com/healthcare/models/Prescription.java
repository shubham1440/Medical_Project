package com.healthcare.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "prescriptions", indexes = {
        @Index(name = "idx_prescription_patient", columnList = "patient_id"),
        @Index(name = "idx_prescription_provider", columnList = "provider_id"),
        @Index(name = "idx_prescription_start_date", columnList = "start_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(name = "medication_name", nullable = false, length = 200)
    private String medicationName;

    @Column(name = "dosage", nullable = false, length = 50)
    private String dosage;

    @Column(name = "route", nullable = false, length = 50)
    private String route;

    @Column(name = "frequency", nullable = false, length = 50)
    private String frequency;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "instructions", length = 1000)
    private String instructions;

    @Column(name = "dispensed", nullable = false)
    private Boolean dispensed = false;

    @Column(name = "dispensed_at")
    private LocalDate dispensedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prior_prescription_id")
    private Prescription priorPrescription;
}
