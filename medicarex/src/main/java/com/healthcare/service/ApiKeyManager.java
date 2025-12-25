package com.healthcare.service;

import com.healthcare.models.ApiClient;
import com.healthcare.repo.ApiClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyManager {
    @Autowired
    private ApiClientRepository repository;

//    @Transactional
//    public boolean authenticate(String key) {
//        // 1. Find the client or throw error if key doesn't exist
//        ApiClient client = (ApiClient) repository.findByApiKey(key)
//                .orElseThrow(() -> new BadCredentialsException("Invalid Security Key"));
//
//        // 2. Check if already locked
//        if (client.isLocked()) {
//            throw new LockedException("Account locked. Please contact: admin@gopalhospital.com");
//        }
//
//        // 3. In your Filter, you will call this method ONLY if the key is valid.
//        // If we are here, it means the key MATCHED.
//        client.setFailedAttempts(0);
//        repository.save(client);
//
//        return true;
//    }
    @Transactional
    public ApiClient authenticateAndGetClient(String key) {
        // 1. Find the client
        ApiClient client = repository.findByApiKey(key)
                .orElseThrow(() -> new BadCredentialsException("Invalid Security Key"));

        // 2. Check if locked
        if (client.isLocked()) {
            throw new LockedException("Account locked. Please contact: admin@gopalhospital.com");
        }

        // 3. Reset failed attempts on successful login
        client.setFailedAttempts(0);
        repository.save(client);

        return client; // Return the full entity
    }

    /**
     * Call this method from your Filter's 'catch' block or when validation fails
     */
    @Transactional
    public void handleFailedAttempt(String key) {
        // findByApiKey returns an Optional<ApiClient>
        repository.findByApiKey(key).ifPresent((ApiClient client) -> {
            int newAttempts = client.getFailedAttempts() + 1;
            client.setFailedAttempts(newAttempts);

            if (newAttempts >= 3) {
                client.setLocked(true);
            }
            repository.save(client);
        });
    }


    @Transactional
    public void recordFailure(String key) {
        repository.findByApiKey(key).ifPresent(client -> {
            int attempts = client.getFailedAttempts() + 1;
            client.setFailedAttempts(attempts);
            if (attempts >= 3) {
                client.setLocked(true);
            }
            repository.save(client);
        });
    }
}
