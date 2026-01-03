package com.healthcare.controller.consents;

import com.healthcare.dto.request.CreateRequestDTO;
import com.healthcare.models.ConsentRequest;
import com.healthcare.service.ConsentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class ConsentProviderController {
    private final ConsentService consentService;

    @PostMapping("/consents/request")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> raiseRequest(@RequestBody CreateRequestDTO dto, Principal principal) {
        try {
            String name = (principal != null) ? principal.getName() : "User";
            ConsentRequest request = consentService.initiateRequest(name, dto);

            // Return 201 Created for better REST compliance
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (Exception e) {
            // HA: Proper error handling prevents the UI from hanging
            return ResponseEntity.badRequest().body("Failed to initiate request: " + e.getMessage());
        }
    }
}