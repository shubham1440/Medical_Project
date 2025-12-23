package com.healthcare.repo;

import com.healthcare.models.LabResult;
import com.healthcare.models.enums.AbnormalFlag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    Page<LabResult> findByLabOrderId(Long labOrderId, Pageable pageable);

    @Query("SELECT lr FROM LabResult lr WHERE lr.labOrder.patient.id = :patientId " +
            "AND lr.abnormalFlag IN ('H', 'L') AND lr.resultDate >= :afterDate")
    List<LabResult> findAbnormalResultsForPatient(
            Long patientId,
            LocalDate afterDate
    );

    Page<LabResult> findByAbnormalFlag(AbnormalFlag flag, Pageable pageable);
}
