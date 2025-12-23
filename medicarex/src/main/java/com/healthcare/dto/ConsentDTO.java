package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentDTO {
    private Long id;
    private String providerName;
    private String providerEmail;
    private String grantedRole;          // Example: "PROVIDER"
    private String permissionType;       // Example: "VIEW_RECORDS"
    private Boolean isActive;            // Example: true/false
    private String grantedAt;            // String format for display: "2025-12-13T10:00"
    private String revokedAt;
    private String notes;
}
