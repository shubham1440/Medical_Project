package com.healthcare.service.Impl;

import com.healthcare.dto.DutyRosterDTO;
import com.healthcare.dto.PatientDTO;
import com.healthcare.dto.ProviderDTO;
import com.healthcare.dto.request.CreateProviderRequest;
import com.healthcare.dto.request.UpdateProviderRequest;
import com.healthcare.dto.response.PageResponse;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.User;
import com.healthcare.models.enums.Role;
import com.healthcare.repo.PatientRepository;
import com.healthcare.repo.ProviderRepository;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.AuditService;
import com.healthcare.service.ProviderService;
import com.healthcare.util.PHIMaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final PHIMaskingUtil phiMaskingUtil;

    /**
     * Create a new provider with user account
     */
    @Transactional
    public ProviderDTO createProvider(CreateProviderRequest request) {
        log.info("Creating new provider with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + phiMaskingUtil.maskEmail( request.getEmail()));
        }

        if (providerRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already registered: " + request.getLicenseNumber());
        }

        // Create user account
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
                .roles(Set.of(Role.PROVIDER))
                .build();

        user = userRepository.save(user);

        Provider provider = Provider.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .specialty(request.getSpecialty())
                .licenseNumber(request.getLicenseNumber())
                .facility(request.getFacility())
                .department(request.getDepartment())
                .build();

        provider = providerRepository.save(provider);

        auditService.logAction("CREATE_PROVIDER", "Provider", provider.getId(),
                "Created provider: " + phiMaskingUtil.maskEmail(provider.getEmail()));

        log.info("Provider created successfully with ID: {}", provider.getId());
        return convertToDTO(provider);
    }

    @Override
    public Integer countAssignedPatients(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(email)));
        Provider provider = providerRepository.getReferenceById(user.getId());

//        List<User> patientList = userRepository.findAllById(provider.;
        return 0;
    }

    @Override
    public List<DutyRosterDTO> getOnDutyStaffForRoster() {
        log.info("Fetching active providers for duty roster");

        List<Provider> activeProviders = providerRepository.findActiveProvidersForRoster();

        return activeProviders.stream()
                .map(this::convertToRosterDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get provider by ID
     */
    @Transactional(readOnly = true)
    public ProviderDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found with ID: " + id));
        auditService.logAction("READ_PROVIDER", "Provider", id, "Read provider details");
        return convertToDTO(provider);
    }

    /**
     * Get all providers with pagination
     */
    @Transactional(readOnly = true)
    public PageResponse<ProviderDTO> getAllProviders(Pageable pageable) {
        Page<Provider> providerPage = providerRepository.findAll(pageable);

        auditService.logAction("LIST_PROVIDERS", "Provider", -1L,
                "Listed providers page: " + providerPage.getNumber());

        return PageResponse.<ProviderDTO>builder()
                .content(providerPage.getContent().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()))
                .page(providerPage.getNumber())
                .size(providerPage.getSize())
                .totalElements(providerPage.getTotalElements())
                .totalPages(providerPage.getTotalPages())
                .last(providerPage.isLast())
                .build();
    }

    /**
     * Search providers by criteria
     */
    @Transactional(readOnly = true)
    public PageResponse<ProviderDTO> searchProviders(
            String specialty,
            String name,
            String licenseNumber,
            Pageable pageable) {

        Page<Provider> providerPage;

        if (licenseNumber != null && !licenseNumber.isEmpty()) {
            Provider provider = providerRepository.findByLicenseNumber(licenseNumber)
                    .orElseThrow(() -> new RuntimeException("Provider not found with license: " + licenseNumber));
            return PageResponse.<ProviderDTO>builder()
                    .content(java.util.List.of(convertToDTO(provider)))
                    .page(0)
                    .size(1)
                    .totalElements(1)
                    .totalPages(1)
                    .last(true)
                    .build();
        } else if (specialty != null && !specialty.isEmpty()) {
            providerPage = providerRepository.findBySpecialtyContainingIgnoreCase(specialty, pageable);
        } else if (name != null && !name.isEmpty()) {
            providerPage = providerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    name, name, pageable);
        } else {
            providerPage = providerRepository.findAll(pageable);
        }

        auditService.logAction("SEARCH_PROVIDERS", "Provider", -1L,
                "Searched providers with criteria: specialty=" + specialty +
                        ", name=" + name + ", licenseNumber=" + licenseNumber);

        return PageResponse.<ProviderDTO>builder()
                .content(providerPage.getContent().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()))
                .page(providerPage.getNumber())
                .size(providerPage.getSize())
                .totalElements(providerPage.getTotalElements())
                .totalPages(providerPage.getTotalPages())
                .last(providerPage.isLast())
                .build();
    }

    /**
     * Update provider information
     */
    @Transactional
    public ProviderDTO updateProvider(Long id, UpdateProviderRequest request) {
        log.info("Updating provider with ID: {}", id);

        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found with ID: " + id));

        // Update fields if provided
        if (request.getFirstName() != null) {
            provider.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            provider.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            provider.setPhone(request.getPhone());
        }
        if (request.getSpecialty() != null) {
            provider.setSpecialty(request.getSpecialty());
        }
        if (request.getFacility() != null) {
            provider.setFacility(request.getFacility());
        }
        if (request.getDepartment() != null) {
            provider.setDepartment(request.getDepartment());
        }

        provider = providerRepository.save(provider);

        // Audit log
        auditService.logAction("UPDATE_PROVIDER", "Provider", provider.getId(),
                "Updated provider: " + provider.getEmail());

        log.info("Provider updated successfully: {}", id);
        return convertToDTO(provider);
    }

    /**
     * Delete provider (soft delete)
     */
    @Transactional
    public void deleteProvider(Long id) {
        log.info("Deleting provider with ID: {}", id);

        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found with ID: " + id));

        provider.setDeleted(true);
        providerRepository.save(provider);

        User user = provider.getUser();
        user.setAccountEnabled(false);
        userRepository.save(user);

        // Audit log
        auditService.logAction("DELETE_PROVIDER", "Provider", provider.getId(),
                "Deleted provider: " + provider.getEmail());

        log.info("Provider deleted successfully: {}", id);
    }

    /**
     * Get provider's patient panel
     */
    @Transactional(readOnly = true)
    public PageResponse<PatientDTO> getProviderPanel(Long providerId, Pageable pageable) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found with ID: " + providerId));

        java.util.List<Patient> panelList = new java.util.ArrayList<>(provider.getPanelPatients());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), panelList.size());

        java.util.List<PatientDTO> content = panelList.subList(start, end).stream()
                .map(this::convertPatientToDTO)
                .collect(Collectors.toList());

        auditService.logAction("READ_PROVIDER_PANEL", "Provider", providerId,
                "Read provider patient panel, page: " + pageable.getPageNumber());

        return PageResponse.<PatientDTO>builder()
                .content(content)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements((long) panelList.size())
                .totalPages((int) Math.ceil((double) panelList.size() / pageable.getPageSize()))
                .last(end >= panelList.size())
                .build();
    }

    /**
     * Convert Provider entity to DTO
     */
    private ProviderDTO convertToDTO(Provider provider) {
        return ProviderDTO.builder()
                .id(provider.getId())
                .firstName(provider.getFirstName())
                .lastName(provider.getLastName())
                .email(provider.getEmail())
                .phone(provider.getPhone())
                .specialty(provider.getSpecialty())
                .licenseNumber(provider.getLicenseNumber())
                .facility(provider.getFacility())
                .department(provider.getDepartment())
                .build();
    }

    /**
     * Convert Patient entity to DTO (simplified)
     */
    private PatientDTO convertPatientToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth().toString())
                .gender(patient.getGender().name())
                .build();
    }

    /**
     * Helper method to map Provider to DutyRosterDTO
     */
    private DutyRosterDTO convertToRosterDTO(Provider provider) {
        String dutyTime = "09:00 AM";
        if (provider.getCreatedAt() != null) {
            try {
                dutyTime = provider.getCreatedAt().format(
                        java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
                );
            } catch (Exception e) {
                log.error("Error formatting date for provider: {}", provider.getId());
            }
        }

        return DutyRosterDTO.builder()
                .id(provider.getId())
                .firstName(provider.getFirstName() != null ? provider.getFirstName() : "Unknown")
                .lastName(provider.getLastName() != null ? provider.getLastName() : "Staff")
                .inDutyTime(dutyTime)
                .status("ONLINE")
                .build();
    }}
