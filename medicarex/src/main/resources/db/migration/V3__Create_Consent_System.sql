-- 1. Create Consent Request Table
CREATE TABLE IF NOT EXISTS consent_requests (
    id BINARY(16) NOT NULL, -- UUID stored as binary for performance
    patient_id BIGINT,
    provider_id BIGINT NOT NULL,
    requested_items JSON,
    status VARCHAR(20) DEFAULT 'PENDING',
    provider_reason VARCHAR(255),
    patient_comment VARCHAR(255),
    expiry_time DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    -- Indexes based on your @Index annotations
    INDEX idx_prescription_patient (patient_id),
    INDEX idx_prescription_provider (provider_id)
) ENGINE=InnoDB;

-- 2. Create Consents Table
CREATE TABLE IF NOT EXISTS consents (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,

    patient_id BIGINT NOT NULL,
    provider_id BIGINT,
    granted_role VARCHAR(20),
    permission_type VARCHAR(20) NOT NULL,
    consent_request_id BINARY(16),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    granted_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6),

    -- Fixed: Matching Document.id type (BIGINT) and removed syntax clutter
    document_id BIGINT,

    consent_status VARCHAR(255) NOT NULL,
    notes VARCHAR(1000),

    PRIMARY KEY (id),

    CONSTRAINT fk_consent_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_consent_document FOREIGN KEY (document_id) REFERENCES documents(id),
    CONSTRAINT fk_consent_provider FOREIGN KEY (provider_id) REFERENCES providers(id),
    CONSTRAINT fk_consent_request FOREIGN KEY (consent_request_id) REFERENCES consent_requests(id),

    INDEX idx_consent_patient (patient_id),
    INDEX idx_consent_provider (provider_id),
    INDEX idx_consent_active (is_active)
) ENGINE=InnoDB;