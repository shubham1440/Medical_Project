package com.healthcare.service.Impl;

import com.healthcare.dto.request.LabOrderRequest;
import com.healthcare.models.LabOrder;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.User;
import com.healthcare.models.enums.LabPriority;
import com.healthcare.repo.LabOrderRepository;
import com.healthcare.repo.PatientRepository;
import com.healthcare.repo.ProviderRepository;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.LabOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LabOrderServiceImpl implements LabOrderService {

    private final LabOrderRepository labOrderRepository;
    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    @Override
    public LabOrder createLabOrder(LabOrderRequest request, String providerEmail) throws Exception {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new RuntimeException("Provider user not found"));
        Provider provider = providerRepository.getUserById(user.getId());
        Patient patient = patientRepository.getReferenceById(request.getPatientId());

        LabOrder labOrder = LabOrder.builder()
                .patient(patient)
                .orderingProvider(provider)
                .testCode(request.getTestName())
                .testName(request.getTestName())
                .priority(LabPriority.ROUTINE)
                .orderDate(LocalDate.now())
                .notes(request.getNotes())
                .build();

        return labOrderRepository.save(labOrder);
    }

    @Override
    public LabOrder getLabOrderById(Long id) throws Exception {
        return labOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab order not found"));
    }

    @Override
    public List<LabOrder> getLabOrdersForPatient(Long patientId) throws Exception {
        return labOrderRepository.findByPatientId(patientId);
    }

    @Override
    public List<LabOrder> getLabOrdersForProvider(Long providerId) throws Exception {
        return labOrderRepository.findByOrderingProviderId(providerId);
    }

    @Override
    public void deleteLabOrder(Long id) throws Exception {
        labOrderRepository.deleteById(id);
    }
}

