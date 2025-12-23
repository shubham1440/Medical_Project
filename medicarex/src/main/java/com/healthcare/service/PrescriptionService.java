package com.healthcare.service;

import com.healthcare.dto.request.PrescriptionListRequest;
import com.healthcare.dto.request.PrescriptionRequest;
import com.healthcare.models.Prescription;
import com.healthcare.models.Provider;

import java.util.List;

public interface PrescriptionService {

    public List<Prescription> createPrescription(PrescriptionListRequest req, String ProviderEmail) throws Exception ;
}
