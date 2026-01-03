package com.healthcare.repo;


import com.healthcare.models.ConsentRequest;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.enums.ConsentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface  ConsentRequestRepository extends JpaRepository<ConsentRequest, UUID> {
    List<ConsentRequest> findByPatient(Patient patient);

    @Query("SELECT cr FROM ConsentRequest cr WHERE cr.provider = :provider AND cr.status IN :statuses")
    List<ConsentRequest> findByProviderAndStatuses(@Param("provider") Provider provider,
                                                   @Param("statuses") List<ConsentStatus> statuses);
    List<ConsentRequest> findByProvider(Provider provider);

    List<ConsentRequest> findByPatientIdAndStatus(Long patientId, ConsentStatus status);

    List<ConsentRequest> findByPatientIdAndStatusOrderByCreatedAtDesc(Long patientId, ConsentStatus status);
}
