package com.healthcare.service;

import com.healthcare.dto.PatientDTO;
import com.healthcare.dto.ProviderDTO;
import java.util.List;

public interface ProviderPanelService {

    void assignPatientToProvider(Long providerId, Long patientId, String actorUserId, String correlationId);

    void removePatientFromProvider(Long providerId, Long patientId, String actorUserId, String correlationId);

    List<PatientDTO> getPatientsForProvider(Long providerId);

//    List<ProviderDTO> getProvidersForPatient(Long patientId);
}
