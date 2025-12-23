package com.healthcare.service.Impl;

import com.healthcare.dto.DocumentDTO;
import com.healthcare.models.ClinicalDocument;
import com.healthcare.models.Patient;
import com.healthcare.repo.ClinicalDocumentRepository;
import com.healthcare.repo.PatientRepository;
import com.healthcare.service.DocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final ClinicalDocumentRepository clinicalDocumentRepository;
    private final PatientRepository patientRepository;

    @Override
    public List<DocumentDTO> getDocumentsForPatient(String email) {
        Patient patient = patientRepository.findByEmail(email);
        List<ClinicalDocument> docs = clinicalDocumentRepository.findByPatientAndIsCurrentTrue(patient);
        return docs.stream()
                .map(DocumentServiceImpl::toDocumentDTO)
                .toList();
    }

    @Override
    public int countDocumentsForPatient(String email) {
        Patient patient = patientRepository.findByEmail(email);
        return clinicalDocumentRepository.countByPatientAndIsCurrentTrue(patient);
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

}

