package com.healthcare.repo;

import com.healthcare.models.Consent;
import com.healthcare.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {

    List<Consent> findByPatientIdAndIsActiveTrue(Long patientId);

    @Query("SELECT c FROM Consent c WHERE c.patient.id = :patientId " +
            "AND c.provider.id = :providerId AND c.isActive = true")
    Optional<Consent> findActiveConsentBetween(Long patientId, Long providerId);

    List<Consent> findByPatientAndIsActiveTrue(Patient patient);

    int countByPatientAndIsActiveTrue(Patient patient);

}
