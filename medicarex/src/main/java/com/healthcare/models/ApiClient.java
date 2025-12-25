package com.healthcare.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "api_client")
public class ApiClient {
    @Id
    private String apiKey;
    private String email;
    private int failedAttempts;
    private boolean locked;
}
