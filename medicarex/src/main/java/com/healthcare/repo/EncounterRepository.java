package com.healthcare.repo;

import com.healthcare.models.Encounter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    Page<Encounter> findByPatientId(Long patientId, Pageable pageable);

    Page<Encounter> findByProviderId(Long providerId, Pageable pageable);

    Optional<Encounter> findByAppointmentId(Long appointmentId);

    @Query("SELECT COUNT(e) FROM Encounter e WHERE e.diagnosisCodes LIKE %:diagnosisCode%")
    long countByDiagnosisCode(String diagnosisCode);
}
