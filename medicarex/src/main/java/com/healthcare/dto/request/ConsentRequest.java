package com.healthcare.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentRequest {

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @NotNull(message = "Permission type is required")
    private String permissionType;

    private String notes;
}
