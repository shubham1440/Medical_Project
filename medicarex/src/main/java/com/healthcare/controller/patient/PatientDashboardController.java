package com.healthcare.controller.patient;


import com.healthcare.dto.*;
import com.healthcare.dto.response.PageResponse;
import com.healthcare.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientDashboardController {

    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final LabResultService labResultService;
    private final DocumentService documentService;
    private final ConsentService consentService;

    @GetMapping("/patient/dashboard")
    public String dashboard(Model model, Authentication auth) {
        String email = auth.getName();

        model.addAttribute("username", email);
        model.addAttribute("role", "PATIENT");

        model.addAttribute("upcomingAppointments",
                appointmentService.getUpcomingAppointments(email).size());
        model.addAttribute("labResults",
                labResultService.countLabResultsForPatient(email));
        model.addAttribute("documents",
                documentService.countDocumentsForPatient(email));
        model.addAttribute("activeConsents",
                consentService.countActiveConsentsForPatient(email));

        Pageable pageable = PageRequest.of(0, 1000);
        PageResponse<ProviderDTO> providerPage = providerService.getAllProviders(pageable);
        model.addAttribute("providers", providerPage.getContent());

        return "dashboard/patient/dashboard";
    }
}

