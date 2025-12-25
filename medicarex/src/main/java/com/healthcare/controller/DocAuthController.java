package com.healthcare.controller;

import com.healthcare.models.ApiClient;
import com.healthcare.service.ApiKeyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
@AllArgsConstructor
public class DocAuthController {
    private final ApiKeyManager apiKeyManager;

    @GetMapping("/docs-login")
    public String loginPage() {
        return "docs-login";
    }

//    @PostMapping("/docs-login")
//    public String handleLogin(@RequestParam String accessKey, HttpServletRequest request) {
//        if (apiKeyManager.authenticate(accessKey)) {
//            request.getSession().setAttribute("DOCS_ACCESS_KEY", accessKey);
//            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                    "API_USER", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCS_USER")));
//            SecurityContextHolder.getContext().setAuthentication(auth);
//            request.getSession().setAttribute(
//                    "SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()
//            );
//            System.out.println("LOG: Access Key Verified. Redirecting to Swagger...");
//            return "redirect:/swagger-ui/index.html";
//        }
//        return "redirect:/docs-login?error=true";
//    }

    @PostMapping("/docs-login")
    public String handleLogin(@RequestParam String accessKey, HttpServletRequest request) {
        try {
            // Use the updated manager method
            ApiClient client = apiKeyManager.authenticateAndGetClient(accessKey);

            // Create/Get Session
            HttpSession session = request.getSession(true);

            // DYNAMIC SESSION EXPIRY:
            // Pull the timeout from the database record
            // Fallback to 1800 (30 mins) if the field is null
//            Integer timeout = client.getSessionTimeoutSeconds();
            Integer timeout= null;
            session.setMaxInactiveInterval(timeout != null ? timeout : 1800);

            // Store standard attributes
            session.setAttribute("DOCS_ACCESS_KEY", accessKey);

            // Set Security Context
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    client.getEmail(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCS_USER")));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            return "redirect:/swagger-ui/index.html";

        } catch (BadCredentialsException | LockedException e) {
            return "redirect:/docs-login?error=true&message=" + e.getMessage();
        }
    }
}