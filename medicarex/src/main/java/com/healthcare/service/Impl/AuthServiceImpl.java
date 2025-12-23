package com.healthcare.service.Impl;

import com.healthcare.config.security.SecurityUserDetailsService;
import com.healthcare.config.security.JwtService;
import com.healthcare.dto.request.CreatePatientRequest;
import com.healthcare.dto.request.LoginRequest;
import com.healthcare.dto.request.RegisterRequest;
import com.healthcare.dto.response.AuthResponse;
import com.healthcare.models.User;
import com.healthcare.models.enums.Role;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.AuditService;
import com.healthcare.service.AuthService;
import com.healthcare.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientService patientService;
    private final AuditService auditService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SecurityUserDetailsService customUserDetailsService;

    @Value("${medicarex.security.jwt.expiration}")
    private long jwtExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws Exception {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + request.getRole());
        }

//        if(Role.PATIENT.equals(role)) {
//
//        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .accountEnabled(true)
                .accountLocked(false)
                .passwordExpired(false)
                .passwordChangedAt(LocalDateTime.now())
                .roles(Set.of(role))
                .build();

        user = userRepository.save(user);

        auditService.logAction(
                "REGISTER_USER",
                "User",
                user.getId(),
                "Registered user: " + user.getEmail() + " with role: " + role.name()
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(
                userDetails,
                user.getId(),
                user.getRoles()
        );
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(role.name())
                .expiresIn(jwtExpiration)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) throws Exception {
        return null;
    }
}

