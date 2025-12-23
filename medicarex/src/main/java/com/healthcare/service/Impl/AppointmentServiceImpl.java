package com.healthcare.service.Impl;

import com.healthcare.dto.AppointmentDTO;
import com.healthcare.dto.request.AppointmentRequest;
import com.healthcare.dto.response.PageResponse;
import com.healthcare.models.Appointment;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.User;
import com.healthcare.models.enums.AppointmentStatus;
import com.healthcare.repo.AppointmentRepository;
import com.healthcare.repo.PatientRepository;
import com.healthcare.repo.ProviderRepository;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.AuditService;
import com.healthcare.util.PHIMaskingUtil;
import com.healthcare.util.excpetion.AppointmentNotFoundException;
import com.healthcare.util.excpetion.ProviderNotFoundException;
import com.healthcare.util.excpetion.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final AuditService auditService;
    private final PHIMaskingUtil phiMaskingUtil;

    public List<AppointmentDTO> getProviderAppointments(String providerEmail){
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + providerEmail));
        Provider provider = providerRepository.getReferenceById(user.getId());
        auditService.logAction(
                "LIST_PROVIDER_APPOINTMENTS",
                "Appointment",
                -1L,
                "Viewed appointments for provider: " + providerEmail
        );
        return toDtoList(appointmentRepository.findByProviderOrderByStartTimeDesc(provider));
    }

    public List<AppointmentDTO> getPatientAppointments(String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + patientEmail));
        Patient  patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user id: " + user.getId()));
        return toDtoList(appointmentRepository.findByPatientOrderByStartTimeDesc(patient));
    }

    public List<AppointmentDTO> getUpcomingAppointments(String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + patientEmail));
        Patient  patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user id: " + user.getId()));

        return toDtoList(appointmentRepository.findUpcomingAppointments(patient, LocalDateTime.now()));
    }

    public List<Provider> getAvailableProviders() {
        auditService.logAction(
                "VIEW_AVAILABLE_PROVIDERS",
                "Provider",
                -1L,
                "Listed all available providers"
        );
        return providerRepository.findAll();
    }

    @Transactional
    public Appointment bookAppointment(String patientEmail, AppointmentRequest dto) {
        log.info("Booking appointment for patient: {}", phiMaskingUtil.maskEmail(patientEmail));
        Patient patient;
        try {
            User user = userRepository.findByEmail(patientEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(patientEmail)));
            patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Patient not found for user id: " + phiMaskingUtil.maskMRN(user.getId().toString())));
            if (patient == null) {
                log.warn("No patient found with email: {}", patientEmail);
                throw new IllegalArgumentException("Patient does not exist: " + phiMaskingUtil.maskEmail(patientEmail));
            }
        } catch (Exception e) {
            log.error("Error fetching patient by email {}: {}", phiMaskingUtil.maskEmail(patientEmail), e.getMessage());
            throw e;
        }

//        Provider provider;
//        try {
//            provider = providerRepository.getReferenceById(dto.getProviderId());
//            if (provider == null) {
//                log.warn("No provider found with id: {}", dto.getProviderId());
//                throw new IllegalArgumentException("Provider does not exist: " + dto.getProviderId());
//            }
//        } catch (Exception e) {
//            log.error("Error fetching provider by id {}: {}", dto.getProviderId(), e.getMessage());
//            throw e;
//        }
        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> {
                    log.warn("No provider found with id: {}", dto.getProviderId());
                    return new IllegalArgumentException("Provider does not exist: " + dto.getProviderId());
                });

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .provider(provider)
                .appointmentdate(dto.getStartTime().toLocalDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getStartTime().plusMinutes(30))
                .reason(dto.getReason())
                .notes(dto.getNotes())
                .status(AppointmentStatus.REQUESTED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Successfully booked appointment id {} for patient {} and provider {}",
                saved.getId(), phiMaskingUtil.maskEmail(patientEmail), provider.getId());

        auditService.logAction(
                "BOOK_APPOINTMENT",
                "Appointment",
                saved.getId(),
                "Booked by patient: " + patientEmail + " with provider ID: " + provider.getId()
        );

        return saved;
    }


    @Transactional
    public Appointment cancelAppointment(Long appointmentId, String patientEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getPatient().getEmail().equals(patientEmail)) {
            throw new SecurityException("You can only cancel your own appointments");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        auditService.logAction(
                "CANCEL_APPOINTMENT",
                "Appointment",
                appointment.getId(),
                "Cancelled by patient: " + patientEmail
        );

        return appointment;
    }

    public Appointment getAppointmentById(Long id) {
        auditService.logAction(
                "VIEW_APPOINTMENT",
                "Appointment",
                id,
                "Viewed appointment"
        );
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    }

    @Override
    public List<AppointmentDTO> getAppointmentByEmail(String UserEmail) {
        User user = userRepository.findByEmail(UserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail( UserEmail)));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user id: " + user.getId()));
        if (patient == null) {
            log.warn("No patient found with email: {}", phiMaskingUtil.maskEmail(UserEmail));
        }
//        List<Appointment> appointmentList = appointmentRepository.findAllByPatient_Id(patient.getId());
//        return toDtoList(appointmentList);
        if (patient == null) {
            log.warn("No patient found with email: {}. Returning empty appointment list.", phiMaskingUtil.maskEmail(UserEmail));
            return Collections.emptyList();
        }

        List<Appointment> appointmentList = appointmentRepository.findAllByPatient_Id(patient.getId());
        return toDtoList(appointmentList);
    }

    @Override
    public PageResponse<AppointmentDTO> getAppointments(Long patientId, Long providerId, String status, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public AppointmentDTO confirmAppointment(Long id, String email) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));

        if (!appointment.getProvider().getEmail().equals(email)) {
            throw new UnauthorizedException("Email does not match appointment record");
        }

        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Appointment is already confirmed");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        auditService.logAction(
                "CONFIRM_APPOINTMENT",
                "Appointment",
                savedAppointment.getId(),
                "Confirmed by provider: " + email
        );

        return toDto(savedAppointment);
    }


    public static AppointmentDTO toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        String patientName = appointment.getPatient() != null
                ? appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName()
                : null;
        String providerName = appointment.getProvider() != null
                ? appointment.getProvider().getFirstName() + " " + appointment.getProvider().getLastName()
                : null;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
                .patientName(patientName)
                .providerId(appointment.getProvider() != null ? appointment.getProvider().getId() : null)
                .providerName(providerName)
                .appointmentdate(appointment.getAppointmentdate())
                .startTime(appointment.getStartTime() != null ? appointment.getStartTime().format(timeFormatter) : null)
                .endTime(appointment.getEndTime() != null ? appointment.getEndTime().format(timeFormatter) : null)
                .status(appointment.getStatus() != null ? appointment.getStatus().name() : null)
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .build();
    }

    public static List<AppointmentDTO> toDtoList(List<Appointment> appointments) {
        if (appointments == null) {
            return List.of();
        }
        return appointments.stream()
                .map(AppointmentServiceImpl::toDto)
                .collect(Collectors.toList());
    }

    public Optional<Appointment> findNextAppointment(List<Appointment> appointments) {
        LocalDateTime now = LocalDateTime.now();
        return appointments.stream()
                .filter(appt -> appt.getStartTime().isAfter(now))
                .min((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()));
    }

    @Override
    public Long countTodaysConfirmedAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(email)));
        Provider provider = providerRepository.getReferenceById(user.getId());
        return appointmentRepository.countByProviderAndStatus(provider, AppointmentStatus.CONFIRMED);
    }

    @Override
    public Long countRequestedAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(email)));
        Provider provider = providerRepository.getReferenceById(user.getId());
        return appointmentRepository.countByProviderAndStatus(provider, AppointmentStatus.REQUESTED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getTodaysConfirmedAppointments(String email) {
        log.info("Fetching today's confirmed appointments for email: {}", phiMaskingUtil.maskEmail(email));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", phiMaskingUtil.maskEmail(email));
                    return new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(email));
                });

        log.debug("User found with ID: {}", user.getId());

        Provider provider = providerRepository.getUserById(user.getId());
        log.debug("Provider found with ID: {}", provider.getId());

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        log.debug("Searching appointments from {} to {}", startOfDay, endOfDay);

        List<Appointment> appointmentList = appointmentRepository
                .findTodaysConfirmedAppointmentsForProvider(provider, startOfDay, endOfDay);

        log.info("Found {} confirmed appointments for provider ID: {} on date: {}",
                appointmentList.size(), provider.getId(), today);

        return toDtoList(appointmentList);
    }


    @Override
    public List<AppointmentDTO> getPendingAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(email)));
        Provider provider = providerRepository.getUserById(user.getId());

        List<Appointment> al = appointmentRepository.findPendingAppointmentsForProvider(provider);
        return toDtoList(al);
    }

    @Transactional
    public void markAsComplete(Long id, String providerEmail) {
        log.info("Provider [{}] attempting to mark appointment [{}] as COMPLETE.", phiMaskingUtil.maskEmail(providerEmail), id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Appointment [{}] not found when attempting completion by provider [{}].", id, phiMaskingUtil.maskEmail(providerEmail));
                    return new IllegalArgumentException("Appointment not found");
                });

        if (!appointment.getProvider().getEmail().equalsIgnoreCase(providerEmail)) {
            log.warn("Unauthorized completion attempt: Provider [{}] does not own appointment [{}].", phiMaskingUtil.maskEmail(providerEmail), id);
            throw new SecurityException("Provider not authorized to complete this appointment");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        auditService.logAction("COMPLETE_APPOINTMENT", "Appointment", id,
                "Marked appointment as completed by provider: " + phiMaskingUtil.maskEmail(providerEmail));
        log.info("Appointment [{}] marked as COMPLETED by provider [{}].", id, phiMaskingUtil.maskEmail(providerEmail));
    }
}
