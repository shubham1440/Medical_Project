package com.healthcare.service;

import com.healthcare.dto.ConsentDTO;

import java.util.List;

public interface ConsentService {
    List<ConsentDTO> getActiveConsentsForPatient(String email);
    int countActiveConsentsForPatient(String email);
}

