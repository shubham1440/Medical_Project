package com.healthcare.service;

import com.healthcare.dto.request.LabOrderRequest;
import com.healthcare.models.LabOrder;
import java.util.List;

public interface LabOrderService {
    LabOrder createLabOrder(LabOrderRequest request, String providerEmail) throws Exception;
    LabOrder getLabOrderById(Long id) throws Exception;
    List<LabOrder> getLabOrdersForPatient(Long patientId) throws Exception;
    List<LabOrder> getLabOrdersForProvider(Long providerId) throws Exception;
    void deleteLabOrder(Long id) throws Exception;
}
