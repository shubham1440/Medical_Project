package com.healthcare.controller.provider;


import com.healthcare.service.AppointmentService;
import com.healthcare.service.ProviderService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('PROVIDER')")
public class ProviderController {

    private final AppointmentService appointmentService;
    private final ProviderService providerService;

    @GetMapping("/provider/dashboard")
    public String dashboard(Model model, Authentication auth) {
        String email = auth.getName();

        model.addAttribute("username", email);
        model.addAttribute("todaysAppointments",
                appointmentService.countTodaysConfirmedAppointments(email));
        model.addAttribute("pendingActions",
                appointmentService.countRequestedAppointments(email));
        model.addAttribute("assignedPatients",
                providerService.countAssignedPatients(email));

        return "dashboard/provider/dashboard";
    }

    @GetMapping("/provider/worklist")
    public String worklist(Model model, Authentication auth) {
        model.addAttribute("appointments",
                appointmentService.getTodaysConfirmedAppointments(auth.getName()));
        return "dashboard/provider/worklist";
    }

    @GetMapping("/provider/pending")
    public String pending(Model model, Authentication auth) {
        model.addAttribute("pendingAppointments",
                appointmentService.getPendingAppointments(auth.getName()));
        return "dashboard/provider/pending";
    }
}

