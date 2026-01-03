package com.healthcare.repo;

import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.User;
import com.healthcare.models.enums.Role;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUser(User user);

    Optional<Provider> findByEmail(String email);

    Optional<Provider> findByLicenseNumber(String licenseNumber);

    List<Provider> findBySpecialty(String specialty);


    List<Provider> findByDepartment(String department);

    List<Provider> findByFacility(String facility);

    List<Provider> findBySpecialtyAndFacility(String specialty, String facility);

    boolean existsByLicenseNumber(@NotBlank(message = "License number is required") String licenseNumber);

    Page<Provider> findBySpecialtyContainingIgnoreCase(String specialty, Pageable pageable);

    Page<Provider> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, String name1, Pageable pageable);

    @Query("SELECT p FROM Provider p WHERE p.user.id = :userId")
    Provider getUserById(@Param("userId") Long userId);


    @Query("SELECT p FROM Provider p JOIN p.user u WHERE p.deleted = false AND u.accountEnabled = true")
    List<Provider> findActiveProvidersForRoster();

    Optional<Provider> findByUserId(Long userId);
}

