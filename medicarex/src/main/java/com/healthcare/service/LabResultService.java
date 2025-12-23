package com.healthcare.service;

import com.healthcare.dto.LabResultDTO;
import java.util.List;

public interface LabResultService {
    List<LabResultDTO> getResultsForPatient(String email);
    int countLabResultsForPatient(String email);
}
