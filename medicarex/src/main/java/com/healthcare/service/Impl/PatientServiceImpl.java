package com.healthcare.service.Impl;

import com.healthcare.dto.PatientDTO;
import com.healthcare.dto.PatientRegistryDTO;
import com.healthcare.dto.request.CreatePatientRequest;
import com.healthcare.dto.response.PageResponse;
import com.healthcare.models.Patient;
import com.healthcare.models.User;
import com.healthcare.models.enums.Role;
import com.healthcare.repo.PatientRepository;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.AuditService;
import com.healthcare.service.PatientService;
import com.healthcare.util.PHIMaskingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PHIMaskingUtil phiMaskingUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuditService auditService;


    @Transactional
    public PatientDTO createPatient(CreatePatientRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .accountEnabled(true)
                .accountLocked(false)
                .passwordExpired(false)
                .roles(Set.of(Role.PATIENT))
                .build();
        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .mrn(request.getMrn())
                .nationalId(request.getNationalId())
                .emergencyContact(request.getEmergencyContact())
                .encryptionVersion(1)
                .lastEncryptedOn(LocalDate.now())
                .build();
        patient = patientRepository.save(patient);

        // *** Audit ***
        auditService.logAction(
                "CREATE_PATIENT",
                "Patient",
                patient.getId(),
                "Created patient: " + patient.getEmail()
        );

        return convertToDTO(patient, true);
    }


    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // *** Audit ***
        auditService.logAction(
                "READ_PATIENT",
                "Patient",
                id,
                "Viewed patient: " + patient.getEmail()
        );
        return convertToDTO(patient, true);
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientDTO> getAllPatients(Pageable pageable) {
        Page<Patient> patientPage = patientRepository.findAll(pageable);

        // *** Audit ***
        auditService.logAction(
                "LIST_PATIENTS",
                "Patient",
                -1L,
                "Listed all patients, page: " + patientPage.getNumber()
        );

        return PageResponse.<PatientDTO>builder()
                .content(patientPage.getContent().stream()
                        .map(p -> convertToDTO(p, true))
                        .collect(Collectors.toList()))
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .last(patientPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientDTO> searchPatients(
            String lastName,
            String mrn,
            Pageable pageable) {
        Page<Patient> patientPage;

        if (mrn != null && !mrn.isEmpty()) {
            Patient patient = patientRepository.findByMrn(mrn)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            patientPage = Page.empty(pageable);

            auditService.logAction(
                    "SEARCH_PATIENTS",
                    "Patient",
                    patient.getId(),
                    "Searched patient by MRN: " + mrn
            );
            return PageResponse.<PatientDTO>builder()
                    .content(java.util.List.of(convertToDTO(patient, true)))
                    .page(0)
                    .size(1)
                    .totalElements(1)
                    .totalPages(1)
                    .last(true)
                    .build();


        } else if (lastName != null && !lastName.isEmpty()) {

            // *** Audit ***
            auditService.logAction(
                    "SEARCH_PATIENTS",
                    "Patient",
                    -1L,
                    "Searched patients by lastName: " + lastName
            );
            patientPage = patientRepository.findByLastNameStartingWithIgnoreCase(
                    lastName, pageable);
        } else {

            // *** Audit ***
            auditService.logAction(
                    "SEARCH_PATIENTS",
                    "Patient",
                    -1L,
                    "Searched all patients (no filter)"
            );
            patientPage = patientRepository.findAll(pageable);
        }

        return PageResponse.<PatientDTO>builder()
                .content(patientPage.getContent().stream()
                        .map(p -> convertToDTO(p, true))
                        .collect(Collectors.toList()))
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .last(patientPage.isLast())
                .build();
    }

    public List<PatientRegistryDTO> getDashboardRegistry() {
        return patientRepository.findAll().stream()
                .filter(p -> p != null)
                .map(p -> PatientRegistryDTO.builder()
                        .id(p.getId())
                        // Correctly map your model's firstName and lastName
                        .fullName((p.getFirstName() != null ? p.getFirstName() : "") + " " +
                                (p.getLastName() != null ? p.getLastName() : ""))
                        // Use MRN or Gender as the 'info' field
                        .info(p.getGender() != null ? p.getGender().toString() : "No Gender Info")
                        .build())
                .collect(Collectors.toList());
    }
    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Update allowed fields
        if (patientDTO.getPhone() != null) {
            patient.setPhone(patientDTO.getPhone());
        }
        if (patientDTO.getAddress() != null) {
            patient.setAddress(patientDTO.getAddress());
        }
        if (patientDTO.getEmergencyContact() != null) {
            patient.setEmergencyContact(patientDTO.getEmergencyContact());
        }

        patient = patientRepository.save(patient);

        // *** Audit ***
        auditService.logAction(
                "UPDATE_PATIENT",
                "Patient",
                patient.getId(),
                "Updated patient: " + patient.getEmail()
        );

        return convertToDTO(patient, false);
    }

    private PatientDTO convertToDTO(Patient patient, boolean maskPHI) {
        return PatientDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(maskPHI ? phiMaskingUtil.maskEmail(patient.getEmail()) : patient.getEmail())
                .phone(maskPHI ? phiMaskingUtil.maskPhone(patient.getPhone()) : patient.getPhone())
                .address(maskPHI ? phiMaskingUtil.maskAddress(patient.getAddress()) : patient.getAddress())
                .dateOfBirth(patient.getDateOfBirth().toString())
                .gender(patient.getGender().name())
                .mrn(maskPHI ? phiMaskingUtil.maskMRN(patient.getMrn()) : patient.getMrn())
                .emergencyContact(patient.getEmergencyContact())
                .build();
    }


}

