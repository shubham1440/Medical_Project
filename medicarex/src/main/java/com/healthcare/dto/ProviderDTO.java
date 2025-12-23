package com.healthcare.dto;

import com.healthcare.models.Provider;
import com.healthcare.util.PHIMaskingUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialty;
    private String licenseNumber;
    private String facility;
    private String department;


    /**
     * Creates a masked ProviderDTO from a Provider entity using the masking util.
     */
    public static ProviderDTO maskedFrom(Provider provider, PHIMaskingUtil maskingUtil) {
        return maskingUtil.maskProvider(provider);
    }
}
