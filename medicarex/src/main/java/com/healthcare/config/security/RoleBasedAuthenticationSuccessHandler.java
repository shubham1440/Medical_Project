package com.healthcare.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        String redirectUrl = determineTargetUrl(authentication);

        log.info("User {} authenticated successfully with roles: {}. Redirecting to: {}",
                username,
                authentication.getAuthorities(),
                redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        log.debug("Determining redirect URL for role: {}", role);

        if (role.equals("PATIENT") || role.equals("ROLE_PATIENT")) {
            return "/patient/dashboard";
        } else if (role.equals("PROVIDER") || role.equals("ROLE_PROVIDER")) {
            return "/provider/dashboard";
        } else if (role.equals("OPS") || role.equals("ROLE_OPS")) {
            return "/ops/dashboard";
        } else if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
            return "/admin/dashboard";
        }

        log.warn("Unknown role: {}. Redirecting to default dashboard", role);
        return "/patient/dashboard";
    }
}

