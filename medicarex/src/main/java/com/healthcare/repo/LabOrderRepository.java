package com.healthcare.repo;

import com.healthcare.models.LabOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {

    Page<LabOrder> findByPatientId(Long patientId, Pageable pageable);
    Page<LabOrder> findByOrderingProviderId(Long providerId, Pageable pageable);

    List<LabOrder> findByPatientId(Long patientId);
    List<LabOrder> findByOrderingProviderId(Long providerId);

    Page<LabOrder> findByOrderDate(LocalDate orderDate, Pageable pageable);
    List<LabOrder> findByOrderDate(LocalDate orderDate);

    List<LabOrder> findByPatientIdAndTestCode(Long patientId, String testCode);

    List<LabOrder> findByOrderDateBetween(LocalDate from, LocalDate to);

}
