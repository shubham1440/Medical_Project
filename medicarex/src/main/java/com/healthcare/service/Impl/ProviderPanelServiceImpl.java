package com.healthcare.service.Impl;

import com.healthcare.dto.PatientDTO;
import com.healthcare.dto.ProviderDTO;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.repo.PatientRepository;
import com.healthcare.repo.ProviderRepository;
import com.healthcare.service.AuditService;
import com.healthcare.service.ProviderPanelService;
import com.healthcare.util.PHIMaskingUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderPanelServiceImpl implements ProviderPanelService {

    private final ProviderRepository providerRepository;
    private final PatientRepository patientRepository;
    private final AuditService auditService;
    private final PHIMaskingUtil maskingUtil;

    @Override
    public void assignPatientToProvider(Long providerId, Long patientId, String actorUserId, String correlationId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        if (provider.getPanelPatients().contains(patient)) {
            // Provider already assigned to this patient
            log.info("Panel assignment skipped [providerId={},patientId={},correlationId={}]", providerId, patientId, correlationId);
            return;
        }

        provider.getPanelPatients().add(patient);
        providerRepository.save(provider);

        auditService.recordEvent(
                actorUserId,
                "ASSIGN_PANEL",
                "ProviderPanel",
                providerId + "-" + patientId,
                correlationId,
                Map.of("providerId", providerId, "patientId", patientId)
        );
        log.info("Panel assignment completed [providerId={},patientId={},correlationId={}]", providerId, patientId, correlationId);
    }

    @Override
    public void removePatientFromProvider(Long providerId, Long patientId, String actorUserId, String correlationId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        if (!provider.getPanelPatients().contains(patient)) {
            log.info("Panel removal skipped [providerId={},patientId={},correlationId={}]", providerId, patientId, correlationId);
            throw new EntityNotFoundException("Panel assignment not found");
        }

        provider.getPanelPatients().remove(patient);
        providerRepository.save(provider);

        auditService.recordEvent(
                actorUserId,
                "REMOVE_PANEL",
                "ProviderPanel",
                providerId + "-" + patientId,
                correlationId,
                Map.of("providerId", providerId, "patientId", patientId)
        );
        log.info("Panel removal completed [providerId={},patientId={},correlationId={}]", providerId, patientId, correlationId);
    }

    @Override
    public List<PatientDTO> getPatientsForProvider(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new EntityNotFoundException("Provider not found"));
        return provider.getPanelPatients().stream()
                .map(patient -> PatientDTO.maskedFrom(patient, maskingUtil))
                .collect(Collectors.toList());
    }
}

