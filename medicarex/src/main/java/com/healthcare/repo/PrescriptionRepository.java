package com.healthcare.repo;

import com.healthcare.models.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Page<Prescription> findByPatientId(Long patientId, Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId " +
            "AND p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<Prescription> findActivePrescriptions(Long patientId, LocalDate currentDate);

    List<Prescription> findByPatientIdAndPriorPrescriptionIsNotNull(Long patientId);

    @Query("SELECT p.priorPrescription FROM Prescription p WHERE p.patient.id = :patientId AND p.startDate = :today")
    Prescription findTodaysPriorPrescription(@Param("patientId") Long patientId, @Param("today") LocalDate today);
}
