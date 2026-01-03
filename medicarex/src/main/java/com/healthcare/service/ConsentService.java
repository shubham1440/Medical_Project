package com.healthcare.service;

import com.healthcare.dto.ConsentDTO;
import com.healthcare.dto.request.ApproveRequestDTO;
import com.healthcare.dto.request.CreateConsentRequestDTO;
import com.healthcare.dto.request.CreateRequestDTO;
import com.healthcare.models.ConsentRequest;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface ConsentService {
    ConsentRequest initiateRequest(String providerEmail, CreateRequestDTO dto);
    public void processDecision(UUID requestId, ApproveRequestDTO dto, boolean approved);
    List<CreateConsentRequestDTO> getPendingRequestsForPatient(String email);
    int countActiveConsentsForPatient(String email);
    public List<ConsentDTO> getAllApprovedConsents(String email);
    @Transactional
    void approveRequest(UUID requestId);
    @Transactional
    void rejectRequest(UUID requestId);
}

