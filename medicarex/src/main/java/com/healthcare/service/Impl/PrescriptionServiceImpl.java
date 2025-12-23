package com.healthcare.service.Impl;

import com.healthcare.dto.request.LabOrderRequest;
import com.healthcare.dto.request.PrescriptionListRequest;
import com.healthcare.dto.request.PrescriptionRequest;
import com.healthcare.models.Patient;
import com.healthcare.models.Prescription;
import com.healthcare.models.Provider;
import com.healthcare.models.User;
import com.healthcare.repo.*;
import com.healthcare.service.LabOrderService;
import com.healthcare.service.LabResultService;
import com.healthcare.service.PrescriptionService;
import com.healthcare.util.PHIMaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabOrderService labOrderService;
    private final PHIMaskingUtil phiMaskingUtil;

    @Override
    public List<Prescription> createPrescription(PrescriptionListRequest req, String providerEmail) throws Exception {
        User user = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + phiMaskingUtil.maskEmail(providerEmail)));
        Provider provider = providerRepository.getUserById(user.getId());
        Patient patient = patientRepository.getReferenceById(req.getPatientId());

        List<Prescription> savedPrescriptions = new ArrayList<>();

        if (req.getMedicines() != null) {
            for (PrescriptionRequest medReq : req.getMedicines()) {
                LocalDate startDate = (medReq.getStartDate() == null || medReq.getStartDate().isBlank())
                        ? LocalDate.now()
                        : LocalDate.parse(medReq.getStartDate());

                LocalDate endDate = startDate.plusDays(medReq.getDurationDays() - 1);

                Prescription priorPrescription = prescriptionRepository.findTodaysPriorPrescription(
                        patient.getId(), startDate);

                if (priorPrescription != null) {
                    log.info("Found prior prescription: {}", priorPrescription.getId());
                } else {
                    log.info("No prior prescription found for patient {} on date {}", patient.getId(), startDate);
                }

                Prescription prescription = Prescription.builder()
                        .patient(patient)
                        .provider(provider)
                        .medicationName(medReq.getMedicationName())
                        .dosage(medReq.getDosage())
                        .route(medReq.getRoute())
                        .frequency(medReq.getFrequency())
                        .durationDays(medReq.getDurationDays())
                        .startDate(startDate)
                        .endDate(endDate)
                        .priorPrescription(priorPrescription)
                        .instructions(medReq.getInstructions())
                        .dispensed(false)
                        .build();

                Prescription saved = prescriptionRepository.save(prescription);
                savedPrescriptions.add(saved);
            }
        }
        Long mainPatientId = req.getPatientId();
        if (req.getTests() != null) {
            for (LabOrderRequest labOrderRequest : req.getTests()) {
                labOrderRequest.setPatientId(mainPatientId);
            }
        }
        labOrderService.createLabOrder(req.getTests().get(0),providerEmail);
        return savedPrescriptions;
    }
}
