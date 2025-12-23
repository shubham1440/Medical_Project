package com.healthcare.util;

import com.healthcare.dto.PatientDTO;
import com.healthcare.dto.ProviderDTO;
import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import org.springframework.stereotype.Component;

@Component
public class PHIMaskingUtil {

    /**
     * Mask email: john.doe@example.com -> j***@e***.com
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];

        String maskedLocal = local.length() > 1
                ? local.charAt(0) + "***"
                : local;

        String maskedDomain = domain.length() > 3
                ? domain.charAt(0) + "***." + domain.substring(domain.lastIndexOf('.') + 1)
                : domain;

        return maskedLocal + "@" + maskedDomain;
    }

    /**
     * Mask phone: +1-555-123-4567 -> +1-***-***-4567
     */
    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }

        String last4 = phone.substring(phone.length() - 4);
        String prefix = phone.substring(0, Math.min(3, phone.length() - 4));
        return prefix + "-***-***-" + last4;
    }

    /**
     * Mask MRN: show first 2 and last 2, mask middle
     */
    public String maskMRN(String mrn) {
        if (mrn == null || mrn.length() <= 4) {
            return mrn;
        }

        String first2 = mrn.substring(0, 2);
        String last2 = mrn.substring(mrn.length() - 2);
        int middleLength = mrn.length() - 4;
        String masked = "*".repeat(middleLength);

        return first2 + masked + last2;
    }

    /**
     * Mask address: show only city and state
     */
    public String maskAddress(String address) {
        if (address == null) {
            return null;
        }
        return "*** (City/State only)";
    }

    /**
     * Mask PHI fields for a Patient object (can be used for DTO, too)
     */
    public PatientDTO maskPatient(Patient patient) {
        if (patient == null) return null;
        return PatientDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(maskEmail(patient.getEmail()))
                .phone(maskPhone(patient.getPhone()))
                .mrn(maskMRN(patient.getMrn()))
                .address(maskAddress(patient.getAddress()))
                .build();
    }

    /**
     * Mask PHI fields for a Provider object
     */
    public ProviderDTO maskProvider(Provider provider) {
        if (provider == null) return null;
        return ProviderDTO.builder()
                .id(provider.getId())
                .firstName(provider.getFirstName())
                .lastName(provider.getLastName())
                .email(maskEmail(provider.getEmail()))
                .phone(maskPhone(provider.getPhone()))
                .build();
    }
}

