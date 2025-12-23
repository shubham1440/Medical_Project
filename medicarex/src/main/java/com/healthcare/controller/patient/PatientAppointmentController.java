package com.healthcare.controller.patient;


import com.healthcare.dto.*;
import com.healthcare.dto.request.AppointmentRequest;
import com.healthcare.dto.response.PageResponse;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.ProviderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientAppointmentController {

    private final AppointmentService appointmentService;
    private final ProviderService providerService;

    @GetMapping("/my")
    public String myAppointments(Model model, Authentication auth) {
        String email = auth.getName();

        List<AppointmentDTO> appointments =
                appointmentService.getPatientAppointments(email);

        Pageable pageable = PageRequest.of(0, 1000);
        PageResponse<ProviderDTO> providers =
                providerService.getAllProviders(pageable);

        model.addAttribute("appointments", appointments);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("username", email);
        model.addAttribute("nextAppointment",
                appointmentService.getUpcomingAppointments(email));

        return "dashboard/patient/appointments";
    }

    @PostMapping("/patient/appointments")
    public String bookAppointment(@ModelAttribute AppointmentRequest request,
                                  Authentication auth,
                                  Model model) {
        try {
            appointmentService.bookAppointment(auth.getName(), request);
            return "redirect:/my";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "dashboard/patient/dashboard";
        }
    }
}

