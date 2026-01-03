package com.healthcare.service;

import com.healthcare.dto.PatientDTO;
import com.healthcare.dto.PatientRegistryDTO;
import com.healthcare.dto.PatientSearchResult;
import com.healthcare.dto.request.CreatePatientRequest;
import com.healthcare.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {

    public PatientDTO createPatient(CreatePatientRequest request) throws Exception;
    PatientDTO getPatientById(Long id) throws Exception;

    PageResponse<PatientDTO> getAllPatients(Pageable pageable) throws Exception;

    PatientDTO updatePatient(Long id, PatientDTO patientDTO) throws Exception;

    PageResponse<PatientDTO> searchPatients(String lastName, String mrn, Pageable pageable) throws Exception;

    List<PatientRegistryDTO> getDashboardRegistry() throws Exception;

    public List<PatientSearchResult> findPatients(String query);
}

