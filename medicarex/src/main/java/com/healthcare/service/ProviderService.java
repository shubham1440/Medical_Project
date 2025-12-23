package com.healthcare.service;

import com.healthcare.dto.DutyRosterDTO;
import com.healthcare.dto.PatientDTO;
import com.healthcare.dto.ProviderDTO;
import com.healthcare.dto.request.CreateProviderRequest;
import com.healthcare.dto.request.UpdateProviderRequest;
import com.healthcare.dto.response.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProviderService {
    PageResponse<PatientDTO> getProviderPanel(Long id, Pageable pageable);

    void deleteProvider(Long id);

    ProviderDTO updateProvider(Long id, @Valid UpdateProviderRequest request);

    PageResponse<ProviderDTO> searchProviders(String specialty, String name, String licenseNumber, Pageable pageable);

    PageResponse<ProviderDTO> getAllProviders(Pageable pageable);

    ProviderDTO getProviderById(Long id);

    ProviderDTO createProvider(@Valid CreateProviderRequest request);

    Integer countAssignedPatients(String email);

    List<DutyRosterDTO> getOnDutyStaffForRoster() throws Exception;
}

