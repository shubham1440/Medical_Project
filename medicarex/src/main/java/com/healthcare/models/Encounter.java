package com.healthcare.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "encounters", indexes = {
        @Index(name = "idx_encounter_patient", columnList = "patient_id"),
        @Index(name = "idx_encounter_provider", columnList = "provider_id"),
        @Index(name = "idx_encounter_appointment", columnList = "appointment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Encounter extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "encounter_date", nullable = false)
    private LocalDateTime encounterDate;

    @Column(name = "chief_complaint", nullable = false, length = 1000)
    private String chiefComplaint;

    @Column(name = "diagnosis_codes", nullable = false, length = 500)
    private String diagnosisCodes;

    @Column(name = "vitals_bp", length = 20)
    private String vitalsBP;

    @Column(name = "vitals_hr", length = 10)
    private String vitalsHR;

    @Column(name = "vitals_temp", length = 10)
    private String vitalsTemp;

    @Column(name = "procedures", length = 1000)
    private String procedures;

    @Column(name = "clinical_notes", length = 4000)
    private String clinicalNotes;

    @Column(name = "signed_by_provider", nullable = false)
    private Boolean signedByProvider = false;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;
}

