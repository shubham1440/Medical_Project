package com.healthcare.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProviderRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String specialty;
    private String facility;
    private String department;
}

