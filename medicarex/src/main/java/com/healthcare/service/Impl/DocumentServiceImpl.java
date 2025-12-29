package com.healthcare.service.Impl;

import com.healthcare.dto.DocumentDTO;
import com.healthcare.models.ClinicalDocument;
import com.healthcare.models.Document;
import com.healthcare.models.Patient;
import com.healthcare.models.User;
import com.healthcare.repo.ClinicalDocumentRepository;
import com.healthcare.repo.DocumentRepository;
import com.healthcare.repo.PatientRepository;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.AuditService;
import com.healthcare.service.DocumentService;
import com.healthcare.util.PHIMaskingUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final ClinicalDocumentRepository clinicalDocumentRepository;
    private final DocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final PHIMaskingUtil phiMaskingUtil;

    @Override
    public List<DocumentDTO> getDocumentsForPatient(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", phiMaskingUtil.maskEmail(email));
                    return new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(email));
                });
        return getPatientDocuments(user);
    }

    @Override
    public int countDocumentsForPatient(String email) {
        Patient patient = patientRepository.findByEmail(email);
        return clinicalDocumentRepository.countByPatientAndIsCurrentTrue(patient);
    }

    @Override
    public DocumentDTO getFileById(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return DocumentDTO.builder()
                .id(doc.getId())
                .title(doc.getFileName())
                .documentType(doc.getContentType())
                .data(doc.getData())
                .build();
    }

    @Transactional
    public void saveDocument(MultipartFile file, String category, String email) throws IOException {
        // Fetch user to get the correct ID
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found: {}", phiMaskingUtil.maskEmail(email));
                    return new UsernameNotFoundException("User not found");
                });

        // Use findById instead of getReferenceById to ensure the patient record exists
        Patient patient = patientRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found for ID: " + user.getId()));

        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setContentType(file.getContentType());
        doc.setFileSize(file.getSize()); // Storing raw long as requested
        doc.setCategory(category);
        doc.setData(file.getBytes());
        doc.setUploadDate(LocalDateTime.now());

        // Linking the verified patient
        doc.setPatient(patient);

        documentRepository.save(doc);
        log.info("Document '{}' securely saved for patient: {}",
                file.getOriginalFilename(), phiMaskingUtil.maskEmail(email));
    }

    public static DocumentDTO toDocumentDTO(ClinicalDocument doc) {
        return DocumentDTO.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .documentType(doc.getDocumentType() != null ? doc.getDocumentType().name() : null)
                .authorName(doc.getAuthor() != null ? doc.getAuthor().getFirstName() : null)
                .documentDate(doc.getDocumentDate() != null ? doc.getDocumentDate().toString() : null)
                .documentPath(doc.getDocumentPath())
                .versionNumber(doc.getVersionNumber())
                .isCurrent(doc.getIsCurrent())
                .changeSummary(doc.getChangeSummary())
                .build();
    }

    public List<DocumentDTO> getPatientDocuments(User user) {
        log.info("Accessing Health Vault for user: {}", user.getEmail());

        // 1. Find the patient first to get their ID
        Patient patient = patientRepository.getReferenceById(user.getId());

        if (patient == null) {
            log.warn("Access Attempt Failed: No patient record found for email {}", user.getEmail());
            return Collections.emptyList();
        }

        log.debug("Found patient ID: {}. Fetching encrypted documents...", patient.getId());

        // 2. Fetch the list of documents belonging to this patient
        List<Document> docs = documentRepository.findByPatientId(patient.getId());

        log.info("Successfully retrieved {} documents for patient {}", docs.size(), patient.getId());

        return docs.stream()
                .map(doc -> {
                    log.trace("Processing document: ID={}, Name={}", doc.getId(), doc.getFileName());

                    return DocumentDTO.builder()
                            .id(doc.getId())
                            .title(doc.getFileName())
                            .documentType(doc.getContentType())
                            .fileSize(formatFileSize(doc.getFileSize()))
                            .category(doc.getCategory())
                            .documentDate(doc.getUploadDate() != null ?
                                    doc.getUploadDate().toLocalDate().toString() : "N/A")
                            .data(doc.getData() != null ?
                                    doc.getData() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " Bytes";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}

