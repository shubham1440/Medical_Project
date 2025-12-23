package com.healthcare.service.Impl;

import com.healthcare.dto.LabResultDTO;
import com.healthcare.service.LabResultService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LabResultServiceImpl implements LabResultService {
    @Override
    public List<LabResultDTO> getResultsForPatient(String email) {
        return List.of();
    }

    @Override
    public int countLabResultsForPatient(String email) {
        return 0;
    }
}
