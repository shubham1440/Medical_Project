package com.healthcare.controller.admin;


import com.healthcare.dto.DutyRosterDTO;
import com.healthcare.dto.PatientRegistryDTO;
import com.healthcare.dto.request.*;
import com.healthcare.dto.response.AuditLogResponse;
import com.healthcare.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProviderService providerService;
    private final PatientService patientService;
    private final AuditService auditService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "ADMIN");
        return "dashboard/admin/dashboard";
    }

    @GetMapping("/admin/providers/new")
    public String createProviderForm(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("providerRequest", new CreateProviderRequest());
        return "dashboard/admin/provider-form";
    }

    @PostMapping("/admin/providers/new")
    public String createProvider(@Valid @ModelAttribute CreateProviderRequest req,
                                 BindingResult br) {
        if (br.hasErrors()) {
            return "dashboard/admin/provider-form";
        }
        providerService.createProvider(req);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/audit")
    public String audit(@RequestParam(required = false) String actor,
                        @RequestParam(required = false) String action,
                        @RequestParam(required = false) String from,
                        @RequestParam(required = false) String to,
                        Model model,
                        Authentication auth) {

        List<AuditLogResponse> logs =
                auditService.getAuditLogs(actor, action, from, to);

        model.addAttribute("logs", logs);
        model.addAttribute("username", auth.getName());
        return "dashboard/admin/audit-log";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/patients/new")
    public String showCreatePatientForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("createPatientRequest", new CreatePatientRequest());
        return "dashboard/admin/patient - form.html";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/patients/new")
    public String handleCreatePatient(
            @Valid @ModelAttribute("createPatientRequest") CreatePatientRequest patientRequest,
            BindingResult bindingResult,
            Model model,
            Authentication authentication) throws Exception {

        model.addAttribute("username", authentication.getName());
        if (bindingResult.hasErrors()) {
            return "dashboard/admin/patient - form.html";
        }
        patientService.createPatient(patientRequest);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/providers/active")
    @ResponseBody
    public List<DutyRosterDTO> getActiveProviders() throws Exception {
        return providerService.getOnDutyStaffForRoster();
    }

    @GetMapping("/admin/patients/registry") // Changed from /recent to /registry
    @ResponseBody
    public List<PatientRegistryDTO> getPatientRegistry() {
        try {
            List<PatientRegistryDTO> registry = patientService.getDashboardRegistry();
            System.out.println("Registry fetched: " + (registry != null ? registry.size() : 0));
            return registry;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }}

