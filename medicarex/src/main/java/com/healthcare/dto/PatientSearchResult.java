package com.healthcare.dto;

import com.healthcare.models.enums.Gender;

import java.time.LocalDate;

public record PatientSearchResult(
        Long id,
        String firstName,
        String lastName,
        String mrn, // We decrypt this for the UI
        LocalDate dob,
        Gender gender
) {}
