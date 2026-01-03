package com.healthcare.service;

import com.healthcare.dto.DocumentDTO;
import com.healthcare.models.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    List<DocumentDTO> getDocumentsForPatient(String email);
    int countDocumentsForPatient(String email);
    DocumentDTO getFileById(Long id);
    public DocumentDTO getByConsentId(Long Id);
    void saveDocument(MultipartFile file, String category, String email) throws IOException;
}
