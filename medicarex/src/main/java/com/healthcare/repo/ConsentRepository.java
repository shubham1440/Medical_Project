package com.healthcare.repo;

import com.healthcare.models.Consent;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.enums.ConsentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    List<Consent> findAllByConsentStatusAndGrantedAtBefore(
            ConsentStatus status,
            LocalDateTime time
    );
    @Query("SELECT count(c) > 0 FROM Consent c " +
            "WHERE c.provider.id = :providerId " +
            "AND c.patient.id = :patientId " +
            "AND c.isActive = true " +
            "AND c.consentRequest.expiryTime > CURRENT_TIMESTAMP")
    boolean hasAccess(Long providerId, Long patientId);

    List<Consent> findByProviderAndIsActiveTrue(Provider provider);

    @Modifying
    @Transactional
    @Query("UPDATE Consent c SET c.consentStatus = com.healthcare.models.enums.ConsentStatus.EXPIRED, " +
            "c.isActive = false " +
            "WHERE c.consentStatus = :status AND c.grantedAt < :now")
    void bulkExpireConsents(@Param("status") ConsentStatus status, @Param("now") LocalDateTime now);

    Optional<Consent> findByPatientAndProvider(Patient patient, Provider provider);

    List<Consent> findByPatientId(Long patientId);

    List<Consent> findByProvider(Provider provider);

}
