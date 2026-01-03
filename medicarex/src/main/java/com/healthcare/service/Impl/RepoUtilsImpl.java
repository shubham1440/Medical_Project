package com.healthcare.service.Impl;

import com.healthcare.models.Patient;
import com.healthcare.models.Provider;
import com.healthcare.models.User;
import com.healthcare.repo.PatientRepository;
import com.healthcare.repo.ProviderRepository;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.RepoUtilsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class RepoUtilsImpl implements RepoUtilsService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final PatientRepository patientRepository;

    public Provider getProviderByEmail(String email){
        User user = getUserByEmail(email);
        return providerRepository.findByUser(user).orElse(null);
    }

    public Patient getPatientByEmail(String email){
        return patientRepository.findByUserId(getUserByEmail(email).getId())
                .orElseThrow(() -> new RuntimeException("Patient not found for user id: " + getUserByEmail(email).getId()));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
