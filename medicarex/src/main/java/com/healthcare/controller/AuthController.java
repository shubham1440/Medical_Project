package com.healthcare.controller;

import com.healthcare.dto.LeaderDTO;
import com.healthcare.dto.LocationDTO;
import com.healthcare.dto.SpecialityDTO;
import com.healthcare.dto.request.CreatePatientRequest;
import com.healthcare.dto.request.RegisterRequest;
import com.healthcare.service.AuthService;
import com.healthcare.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@AllArgsConstructor
public class AuthController {

//    private final AuthService authService;
    private final PatientService patientService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new CreatePatientRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerRequest") @Valid CreatePatientRequest request,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            patientService.createPatient(request);
            return "fragments/registration-success";
        } catch (Exception e) {
            model.addAttribute("registrationError", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/registration-success")
    public String showSuccessPage() {
        return "fragments/registration-success";
    }

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/my";
        }

        addHomeData(model);
        return "home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        addHomeData(model);
        return "home";
    }

    private void addHomeData(Model model) {
        List<SpecialityDTO> specialities = Arrays.asList(
                new SpecialityDTO("Cardiac Care", "bi bi-heart-pulse text-danger", "cardiac"),
                new SpecialityDTO("Cancer Care", "bi bi-shield-plus text-primary", "cancer"),
                new SpecialityDTO("Neurosciences", "bi bi-brain text-info", "neuro"),
                new SpecialityDTO("Gastrosciences", "bi bi-capsule text-warning", "gastro"),
                new SpecialityDTO("Orthopaedics", "bi bi-person-arms-up text-success", "ortho"),
                new SpecialityDTO("Renal Care", "bi bi-droplet text-info", "renal")
        );
        model.addAttribute("specialityList", specialities);

        model.addAttribute("hospitalCount", "74+");
        model.addAttribute("doctorCount", "13,000+");
        model.addAttribute("labCount", "2,300+");

        List<LocationDTO> locations = Arrays.asList(
                new LocationDTO("Delhi", "2 Locations", "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=400"),
                new LocationDTO("Chennai", "13 Locations", "https://images.unsplash.com/photo-1599661046289-e31897846e41?w=400"),
                new LocationDTO("Hyderabad", "4 Locations", "https://images.unsplash.com/photo-1605000797439-7ef15458f0a6?w=400"),
                new LocationDTO("Mumbai", "6 Locations", "https://images.unsplash.com/photo-1566552881560-0be862a7c445?w=400")
        );
        model.addAttribute("locationList", locations);

        model.addAttribute("icuBeds", 14);
        model.addAttribute("icuPercent", 75);
        model.addAttribute("generalBeds", 42);
        model.addAttribute("generalPercent", 50);

        model.addAttribute("topTPAs", Arrays.asList("Star Health", "HDFC Ergo", "ICICI Lombard"));
        model.addAttribute("hiddenTPAs", Arrays.asList("Niva Bupa", "Care Health", "Bajaj Allianz", "Reliance", "SBI General"));

        List<LeaderDTO> leaders = Arrays.asList(
                new LeaderDTO("Dr. Rajan Gopal", "Founder & Chairman", "https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=400"),
                new LeaderDTO("Sarah Jenkins", "Chief Operations Officer", "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400"),
                new LeaderDTO("Dr. Amit Shah", "Chief Medical Officer", "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400")
        );
        model.addAttribute("leaders", leaders);
    }
}

