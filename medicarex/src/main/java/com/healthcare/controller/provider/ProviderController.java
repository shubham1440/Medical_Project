package com.healthcare.controller.provider;


import com.healthcare.dto.AppointmentDTO;
import com.healthcare.models.Appointment;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.ProviderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('PROVIDER')")
public class ProviderController {

    private final AppointmentService appointmentService;
    private final ProviderService providerService;

    @GetMapping("/provider/dashboard")
    public String dashboard(Model model, Authentication auth) {
        String email = auth.getName();
        List<AppointmentDTO> appointments = appointmentService.getTodaysConfirmedAppointments(email);

        model.addAttribute("username", email);
        model.addAttribute("todaysAppointments",
                appointmentService.countTodaysConfirmedAppointments(email));
        model.addAttribute("pendingActions",
                appointmentService.countRequestedAppointments(email));
        model.addAttribute("assignedPatients",
                providerService.countAssignedPatients(email));
        model.addAttribute("appointments", appointments != null ? appointments : Collections.emptyList());
        return "dashboard/provider/dashboard";
    }

    @GetMapping("/provider/worklist")
    public String getProviderWorklist(
            @RequestParam(value = "date", required = false) String date,
            Model model,
            Authentication auth) {

        LocalDate selectedDate;
        try {
            selectedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        } catch (DateTimeParseException e) {
            selectedDate = LocalDate.now();
        }

        // auth.getName() provides the logged-in provider's email
        List<Appointment> appointments = appointmentService
                .getConfirmedAppointmentsByDate(auth.getName(), selectedDate);

        model.addAttribute("appointments", appointments);
        model.addAttribute("selectedDate", selectedDate.toString());

        return "dashboard/provider/worklist";
    }


    @GetMapping("/provider/pending")
    public String pending(Model model, Authentication auth) {
        model.addAttribute("pendingAppointments",
                appointmentService.getPendingAppointments(auth.getName()));
        return "dashboard/provider/pending";
    }

//    @GetMapping("/provider/appointments")
//    public String getAppointments(@RequestParam(value = "date", required = false) String date, Model model) {
//        LocalDate selectedDate;
//        try {
//            selectedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
//        } catch (DateTimeParseException e) {
//            selectedDate = LocalDate.now();
//        }
//
//        List<Appointment> appointments = appointmentService.findByDate(selectedDate);
//
//        model.addAttribute("appointments", appointments);
//        model.addAttribute("selectedDate", selectedDate.toString());
//
//        return "dashboard/provider/worklist";
//    }

//    @GetMapping("/prescriptions/download/{appointmentId}")
//    public ResponseEntity<byte[]> downloadPrescription(@PathVariable Long appointmentId) {
//        // 1. Fetch prescription data from DB using appointmentId
//        // 2. Generate PDF using a library like OpenPDF or Thymeleaf-to-PDF
//        byte[] pdfContents = prescriptionService.generatePrescriptionPdf(appointmentId);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription_" + appointmentId + ".pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdfContents);
//    }
}

