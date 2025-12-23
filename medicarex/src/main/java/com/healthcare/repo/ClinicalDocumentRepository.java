package com.healthcare.repo;

import com.healthcare.models.ClinicalDocument;
import com.healthcare.models.Patient;
import com.healthcare.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalDocumentRepository extends JpaRepository<ClinicalDocument, Long> {

    Page<ClinicalDocument> findByPatientId(Long patientId, Pageable pageable);

    Page<ClinicalDocument> findByPatientIdAndDocumentType(
            Long patientId,
            DocumentType documentType,
            Pageable pageable
    );

    @Query("SELECT cd FROM ClinicalDocument cd WHERE cd.documentPath LIKE :pattern")
    List<ClinicalDocument> findByPathPattern(String pattern);

    List<ClinicalDocument> findByPatientIdAndIsCurrentTrue(Long patientId);

    List<ClinicalDocument> findByPatientAndIsCurrentTrue(Patient patient);

    int countByPatientAndIsCurrentTrue(Patient patient);
}
