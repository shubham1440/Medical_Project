package com.gopal.medicarex.ops_orchestrator.models.enums;

public enum PatientState {
    ADMITTED,           // Initial state
    BILLING_PENDING,    // Waiting for Finance Service
    PAYMENT_CONFIRMED,  // Finance responded SUCCESS
    BED_RELEASE_PENDING,// Waiting for Bed Service
    DISCHARGED,         // End state
    ERROR_ROLLBACK      // Compensation state if a service fails
}
