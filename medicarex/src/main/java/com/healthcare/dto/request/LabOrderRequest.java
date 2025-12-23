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
public class LabOrderRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    @NotBlank(message = "Test name is required")
    private String testName;
    @NotNull(message = "Priority is required")
    private String priority;
    private String notes;
}
