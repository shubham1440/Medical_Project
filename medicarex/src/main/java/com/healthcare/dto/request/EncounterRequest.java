package com.healthcare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    @NotBlank(message = "Diagnosis codes are required")
    private String diagnosisCodes;

    private String vitalsBP;

    private String vitalsHR;

    private String vitalsTemp;

    private String procedures;

    private String clinicalNotes;
}

