package com.healthcare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientRegistryDTO {
    private Long id;
    private String fullName;
    private String info;
}
