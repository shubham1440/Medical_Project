package com.healthcare.service;

import com.healthcare.dto.DocumentDTO;

import java.util.List;

public interface DocumentService {
    List<DocumentDTO> getDocumentsForPatient(String email);
    int countDocumentsForPatient(String email);
}
