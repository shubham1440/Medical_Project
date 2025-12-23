package com.healthcare.service.Impl;

import com.healthcare.dto.ConsentDTO;
import com.healthcare.models.Consent;
import com.healthcare.models.Patient;
import com.healthcare.repo.ConsentRepository;
import com.healthcare.repo.PatientRepository;
import com.healthcare.service.ConsentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRepository consentRepository;
    private final PatientRepository patientRepository;

    @Override
    public List<ConsentDTO> getActiveConsentsForPatient(String email) {
        Patient patient = patientRepository.findByEmail(email);

        List<Consent> consents = consentRepository.findByPatientAndIsActiveTrue(patient);

        return consents.stream()
                .map(this::toConsentDTO)
                .toList();
    }

    @Override
    public int countActiveConsentsForPatient(String email) {
        Patient patient = patientRepository.findByEmail(email);
        return consentRepository.countByPatientAndIsActiveTrue(patient);
    }

    private ConsentDTO toConsentDTO(Consent consent) {
        return ConsentDTO.builder()
                .id(consent.getId())
                .providerName(
                        consent.getProvider() != null ? consent.getProvider().getFirstName() : ""
                )
                .providerEmail(
                        consent.getProvider() != null ? consent.getProvider().getEmail() : ""
                )
                .grantedRole(
                        consent.getGrantedRole() != null ? consent.getGrantedRole().name() : ""
                )
                .permissionType(
                        consent.getPermissionType() != null ? consent.getPermissionType().name() : ""
                )
                .isActive(consent.getIsActive())
                .grantedAt(consent.getGrantedAt() != null ? consent.getGrantedAt().toString() : null)
                .revokedAt(consent.getRevokedAt() != null ? consent.getRevokedAt().toString() : null)
                .notes(consent.getNotes())
                .build();
    }

}

