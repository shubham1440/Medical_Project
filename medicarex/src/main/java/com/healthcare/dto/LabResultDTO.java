package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultDTO {
    private Long id;
    private Long labOrderId;
    private String testName;
    private String resultCode;
    private String value;
    private String unit;
    private String referenceRange;
    private String abnormalFlag;
    private String resultDate;
}
