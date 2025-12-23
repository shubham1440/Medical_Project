package com.healthcare.repo;

import com.healthcare.models.Appointment;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.User;
import com.healthcare.models.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientOrderByStartTimeDesc(Patient patient);

    List<Appointment> findByProviderOrderByStartTimeDesc(Provider provider);

    List<Appointment> findByPatientAndStatus(Patient patient, AppointmentStatus status);

    List<Appointment> findByProviderAndStatus(Provider provider, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND a.startTime >= :fromDateTime " +
            "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointments(
            @Param("patient") Patient patient,
            @Param("fromDateTime") LocalDateTime fromDateTime
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider = :provider " +
            "AND a.status = com.healthcare.models.enums.AppointmentStatus.CONFIRMED " +
            "AND a.startTime >= :startOfDay " +
            "AND a.startTime <= :endOfDay " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findTodaysConfirmedAppointmentsForProvider(
            @Param("provider") Provider provider,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider = :provider " +
            "AND a.status = com.healthcare.models.enums.AppointmentStatus.REQUESTED " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findPendingAppointmentsForProvider(@Param("provider") Provider provider);



    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND a.startTime < :toDateTime " +
            "ORDER BY a.startTime DESC")
    List<Appointment> findPastAppointments(
            @Param("patient") Patient patient,
            @Param("toDateTime") LocalDateTime toDateTime
    );

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND a.startTime BETWEEN :startDate AND :endDate " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findByPatientAndDateRange(
            @Param("patient") Patient patient,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND a.appointmentdate = :date " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findByPatientAndAppointmentDate(
            @Param("patient") Patient patient,
            @Param("date") LocalDate date
    );

    @Query("SELECT a FROM Appointment a WHERE a.provider = :provider " +
            "AND a.appointmentdate = :date " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findByProviderAndAppointmentDate(
            @Param("provider") Provider provider,
            @Param("date") LocalDate date
    );

    @Query("SELECT a FROM Appointment a WHERE a.provider = :provider " +
            "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
            "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findOverlappingAppointments(
            @Param("provider") Provider provider,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<Appointment> findAllByPatient_Id(Long patientId);

    Long countByPatientAndStatus(Patient patient, AppointmentStatus status);

    Long countByProviderAndStatus(Provider provider, AppointmentStatus status);
}

