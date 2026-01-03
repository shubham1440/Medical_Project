package com.healthcare.service.Impl;

import com.healthcare.dto.ConsentDTO;
import com.healthcare.dto.request.ApproveRequestDTO;
import com.healthcare.dto.request.CreateConsentRequestDTO;
import com.healthcare.dto.request.CreateRequestDTO;
import com.healthcare.models.*;
import com.healthcare.models.enums.ConsentStatus;
import com.healthcare.models.enums.PermissionType;
import com.healthcare.models.enums.Role;
import com.healthcare.repo.*;
import com.healthcare.service.AuditService;
import com.healthcare.service.ConsentService;
import com.healthcare.service.RepoUtilsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRepository consentRepository;
    private final ConsentRequestRepository requestRepository;
    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private RepoUtilsService repoUtils;
    private final AuditService auditService;

    @Override
    public ConsentRequest initiateRequest(String providerEmail, CreateRequestDTO dto) {
        ConsentRequest request = new ConsentRequest();
        log.error("initiateRequest provider email {}, id {} ",providerEmail,repoUtils.getProviderByEmail(providerEmail).getId());
        request.setProvider(
                providerRepository.getReferenceById(repoUtils.getProviderByEmail(providerEmail).getId())
        );
        request.setPatient(patientRepository.getReferenceById(dto.patientId()));
        request.setProviderReason(dto.reason());
        request.setRequestedItems(dto.items());
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public void processDecision(UUID requestId, ApproveRequestDTO dto, boolean approved) {
        log.info("Processing consent decision for requestId: {} | Approved: {}", requestId, approved);

        ConsentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("ConsentRequest not found for ID: {}", requestId);
                    return new RuntimeException("Request Not Found");
                });

        if (approved) {
            log.info("Approval path initiated for request: {}", requestId);

            request.setStatus(ConsentStatus.APPROVED);
            request.setExpiryTime(dto.expiryTime());
            request.setPatientComment(dto.comment());

            Document document = documentRepository.findById(dto.documentId())
                    .orElseThrow(() -> {
                        log.error("Document not found for ID: {}", dto.documentId());
                        return new RuntimeException("Document Not Found");
                    });

            log.info("Building Consent - Patient ID: {}, Provider ID: {}, Document ID: {}",
                    request.getPatient() != null ? request.getPatient().getId() : "NULL",
                    request.getProvider() != null ? request.getProvider().getId() : "NULL",
                    document.getId());

            Consent activeConsent = Consent.builder()
                    .patient(patientRepository.getReferenceById(request.getPatient().getId()))
                    .provider(providerRepository.getReferenceById(request.getProvider().getId()))
                    .consentRequest(request)
                    .isActive(true)
                    .grantedAt(LocalDateTime.now())
                    .notes(dto.comment())
                    .document(document)
                    .consentStatus(ConsentStatus.APPROVED)
                    .revokedAt(dto.expiryTime())
                    .permissionType(PermissionType.VIEW)
                    .grantedRole(Role.PROVIDER)
                    .build();

            try {
                consentRepository.save(activeConsent);
                log.info("Successfully saved active consent for requestId: {}", requestId);
            } catch (Exception e) {
                log.error("Database error while saving Consent: {}", e.getMessage());
                throw e; // Re-throw to trigger @Transactional rollback
            }
            auditService.logAction("approve_consent_Request",null,request.getPatient().getId(),dto.comment());
        }
        else {
            log.info("Request {} was denied by patient.", requestId);
            request.setStatus(ConsentStatus.DENIED);
        }

        requestRepository.save(request);
        log.info("Updated ConsentRequest status saved for requestId: {}", requestId);
    }

    @Override
    public List<CreateConsentRequestDTO> getPendingRequestsForPatient(String email) {
        log.info("Fetching consent requests for account: {}", email);

        Patient patient = null;
        Provider provider = null;


        try {
            patient = repoUtils.getPatientByEmail(email);
        } catch (Exception e) {
            log.debug("Email {} not found in Patient records, proceeding to check Provider records.", email);
        }


        try {
            provider = repoUtils.getProviderByEmail(email);
        } catch (Exception e) {
            log.debug("Email {} not found in Provider records.", email);
        }

        try {
            List<ConsentRequest> requests = new ArrayList<>();

            if (patient != null) {
                log.debug("Processing as Patient. ID: {}", patient.getId());
                requests = requestRepository.findByPatientIdAndStatus(patient.getId(), ConsentStatus.PENDING);
            }
            else if (provider != null) {
                log.debug("Processing as Provider. ID: {}", provider.getId());
                requests = requestRepository.findByProviderAndStatuses(provider,List.of(ConsentStatus.PENDING,ConsentStatus.DENIED));
            }
            else {
                log.warn("Identity resolution failed for email: {}. No records found in any table.", email);
                return Collections.emptyList();
            }

            log.info(" Requests found: {}", requests.size());

            if (requests.isEmpty()) {
                return Collections.emptyList();
            }

            return toCreateRequestDTOlist(requests);

        } catch (Exception e) {
            log.error("CRITICAL ERROR Pending Request : Mapping or Repository failure for {}. Error: {}", email, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public int countActiveConsentsForPatient(String email) {
        Patient patient = patientRepository.findByEmail(email);
        return consentRepository.countByPatientAndIsActiveTrue(patient);
    }

    @Override
    public List<ConsentDTO> getAllApprovedConsents(String email) {
        Patient patient = null;
        Provider provider = null;


        try {
            patient = repoUtils.getPatientByEmail(email);
        } catch (Exception e) {
            log.debug("User {} is not a patient, checking provider status...", email);
        }


        try {
            provider = repoUtils.getProviderByEmail(email);
        } catch (Exception e) {
            log.debug("User {} is not a provider.", email);
        }

        List<Consent> consents = new ArrayList<>();

        if (patient != null) {
            consents = consentRepository.findByPatientIdAndIsActiveTrue(patient.getId());
        } else if (provider != null) {
            consents = consentRepository.findByProviderAndIsActiveTrue(provider);
        } else {
            log.warn("Identity could not be resolved for: {}", email);
            return Collections.emptyList();
        }

        return toConsentDTOlist(consents);
    }

    @Transactional
    @Override
    public void approveRequest(UUID requestId) {
        ConsentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + requestId));

        request.setStatus(ConsentStatus.APPROVED);
        requestRepository.save(request);

        Consent consent = consentRepository.findByPatientAndProvider(request.getPatient(), request.getProvider())
                .orElse(new Consent());

        consent.setPatient(request.getPatient());
        consent.setProvider(request.getProvider());
        consent.setIsActive(true);
        consent.setGrantedAt(LocalDateTime.now());
        consent.setGrantedRole(Role.PROVIDER);
        consent.setPermissionType(PermissionType.VIEW);
        consent.setConsentRequest(request);

        consentRepository.save(consent);
    }

    @Transactional
    @Override
    public void rejectRequest(UUID requestId) {
        ConsentRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with ID: " + requestId));

        // Simply mark the request as rejected
        request.setStatus(ConsentStatus.DENIED);
        requestRepository.save(request);

        // Optional: If a previous active consent existed, you might want to revoke it here
    }

    private List<CreateConsentRequestDTO> toCreateRequestDTOlist(List<ConsentRequest> requests) {
        if (requests == null) return Collections.emptyList();

        return requests.stream()
                .map(request -> {
                    String fullName = "Unknown Provider";
                    Long providerId = null;

                    try {
                        Provider provider = providerRepository.getReferenceById(request.getProvider().getId());
                        fullName = "Dr. " + provider.getFirstName() + " " + provider.getLastName();
                        providerId = request.getProvider().getId();
                        log.debug("Successfully verified Provider ID: {}", providerId);
                    } catch (Exception e) {
                        log.error("DATA CORRUPTION: Request {} points to missing Provider ID 8. Using fallback.",
                                request.getId());
                    }

                    return new CreateConsentRequestDTO(
                            request.getId(),
                            fullName,
                            providerId,
                            request.getStatus(),
                            request.getProviderReason(),
                            request.getRequestedItems(),
                            request.getCreatedAt()
                    );
                })
                .toList();
    }

    private List<ConsentDTO> toConsentDTOlist(List<Consent> consents) {
        if (consents == null) return new ArrayList<>();

        List<ConsentDTO> consentDTOs = new ArrayList<>(consents.size());
        for (Consent consent : consents) {
            consentDTOs.add(toConsentDTO(consent));
        }
        return consentDTOs;
    }

    private ConsentDTO toConsentDTO(Consent consent) {
        return ConsentDTO.builder()
                .id(consent.getId())
                .patientName(consent.getPatient().getFirstName() + " " + consent.getPatient().getLastName())
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

