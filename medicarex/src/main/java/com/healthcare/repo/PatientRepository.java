package com.healthcare.repo;

import com.healthcare.dto.PatientSearchResult;
import com.healthcare.models.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByMrn(String mrn);
    boolean existsByMrn(String mrn);

    Page<Patient> findByLastNameStartingWithIgnoreCase(String lastName, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    Page<Patient> findByDateOfBirthRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    Optional<Patient> findByUserId(Long userId);

    Patient findByEmail(String email);

    @Query("SELECT new com.healthcare.dto.PatientSearchResult(p.id, p.firstName, p.lastName, p.mrn, p.dateOfBirth, p.gender) " +
            "FROM Patient p WHERE p.id = :id")
    List<PatientSearchResult> findByIdCustom(@Param("id") Long id);

    // Optimized with a GIN or B-Tree index on fullName
    @Query("SELECT new com.healthcare.dto.PatientSearchResult(p.id, p.firstName, p.lastName, p.mrn, p.dateOfBirth, p.gender) " +
            "FROM Patient p " +
            "WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PatientSearchResult> findByNameContainingIgnoreCase(@Param("name") String name);


    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Patient> searchByNames(@Param("q") String q);
}
