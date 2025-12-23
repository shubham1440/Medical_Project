package com.healthcare.models;

import com.healthcare.config.security.PHIEncryptor;
import com.healthcare.models.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patient_mrn", columnList = "mrn", unique = true),
        @Index(name = "idx_patient_last_name", columnList = "last_name"),
        @Index(name = "idx_patient_dob", columnList = "date_of_birth")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Convert(converter = PHIEncryptor.class)
    @Column(name = "email_encrypted", nullable = false, length = 500)
    private String email;

    @Convert(converter = PHIEncryptor.class)
    @Column(name = "phone_encrypted", length = 500)
    private String phone;

    @Convert(converter = PHIEncryptor.class)
    @Column(name = "address_encrypted", length = 1000)
    private String address;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Convert(converter = PHIEncryptor.class)
    @Column(name = "mrn_encrypted", nullable = false, unique = true, length = 500)
    private String mrn;

    @Convert(converter = PHIEncryptor.class)
    @Column(name = "national_id_encrypted", length = 500)
    private String nationalId;

    @Column(name = "emergency_contact", length = 500)
    private String emergencyContact;

    @Column(name = "encryption_version")
    private Integer encryptionVersion = 1;

    @Column(name = "last_encrypted_on")
    private LocalDate lastEncryptedOn;
}

