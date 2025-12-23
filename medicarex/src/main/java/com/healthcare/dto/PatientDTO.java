package com.healthcare.dto;

import com.healthcare.models.Patient;
import com.healthcare.util.PHIMaskingUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private Long id;

    private String firstName;

    private String lastName;

    /*
        Will be masked
     */
    private String email;

    /*
        Will be masked
     */
    private String phone;

    /*
        Will be masked
     */
    private String address;

    private String dateOfBirth;

    private String gender;


    /*
        Will be masked
     */
    private String mrn;

    private String emergencyContact;

    /**
     * Creates a masked PatientDTO from a Patient entity using the masking util.
     */
    public static PatientDTO maskedFrom(Patient patient, PHIMaskingUtil maskingUtil) {
        return maskingUtil.maskPatient(patient);
    }
}
