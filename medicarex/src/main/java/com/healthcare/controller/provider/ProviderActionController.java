package com.healthcare.controller.provider;


import com.healthcare.dto.request.PrescriptionListRequest;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.PrescriptionService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('PROVIDER')")
public class ProviderActionController {

    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    @PostMapping("/provider/appointments/{id}/confirm")
    public String confirm(@PathVariable Long id,
                          Authentication auth,
                          RedirectAttributes ra) {
        appointmentService.confirmAppointment(id, auth.getName());
        ra.addFlashAttribute("success", "Appointment confirmed!");
        return "redirect:/provider/pending";
    }

    @PostMapping("/provider/appointments/{id}/cancel")
    public String cancel(@PathVariable Long id,
                         Authentication auth,
                         RedirectAttributes ra) {
        appointmentService.cancelAppointment(id, auth.getName());
        ra.addFlashAttribute("success", "Appointment cancelled!");
        return "redirect:/provider/pending";
    }

    @PostMapping("/provider/appointments/{id}/complete")
    public String complete(@PathVariable Long id, Authentication auth) {
        appointmentService.markAsComplete(id, auth.getName());
        return "redirect:/provider/worklist";
    }

    @PostMapping("/provider/prescriptions")
    public String createPrescription(@ModelAttribute PrescriptionListRequest request,
                                     Authentication auth,
                                     RedirectAttributes ra) throws Exception {
        prescriptionService.createPrescription(request, auth.getName());
        ra.addFlashAttribute("success", "Prescription saved!");
        return "redirect:/provider/worklist";
    }
}

