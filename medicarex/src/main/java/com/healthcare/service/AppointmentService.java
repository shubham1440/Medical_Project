package com.healthcare.service;

import com.healthcare.dto.AppointmentDTO;
import com.healthcare.dto.request.AppointmentRequest;
import com.healthcare.dto.response.PageResponse;
import com.healthcare.models.Appointment;
import com.healthcare.models.Provider;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    public List<AppointmentDTO> getPatientAppointments(String patientEmail);

    public List<AppointmentDTO> getProviderAppointments(String providerEmail);

    public List<AppointmentDTO> getUpcomingAppointments(String patientEmail);

    public List<Provider> getAvailableProviders();

    public Appointment bookAppointment(String patientEmail, AppointmentRequest dto);

    public Appointment cancelAppointment(Long appointmentId, String patientEmail);

    public Appointment getAppointmentById(Long id);

    public List<AppointmentDTO> getAppointmentByEmail(String UserEmail);

    PageResponse<AppointmentDTO> getAppointments(Long patientId, Long providerId, String status, Pageable pageable);

    AppointmentDTO confirmAppointment(Long id, String email);

    public Optional<Appointment> findNextAppointment(List<Appointment> appointments);

    public Long countTodaysConfirmedAppointments(String email);

    public Long countRequestedAppointments(String email);

    List<AppointmentDTO> getTodaysConfirmedAppointments(String email);

    List<AppointmentDTO> getPendingAppointments(String email);

    void markAsComplete(Long id, String providerEmail);

}

