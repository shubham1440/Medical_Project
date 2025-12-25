package com.healthcare.repo;

import com.healthcare.models.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiClientRepository extends JpaRepository<ApiClient, String> {
    Optional<ApiClient> findByApiKey(String apiKey);}
