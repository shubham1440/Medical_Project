-- V3: Schema for Health Vault (Immutable Records)
-- Created: 2025-12-29

CREATE TABLE IF NOT EXISTS `documents` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `file_name` VARCHAR(255) NOT NULL COMMENT 'Original name of the clinical file',
    `content_type` VARCHAR(100) NOT NULL COMMENT 'MIME type for browser rendering (PDF/JPG)',
    `file_size` BIGINT NOT NULL COMMENT 'Size in bytes',
    `category` VARCHAR(50) DEFAULT 'General' COMMENT 'Radiology, Lab, Prescription, etc.',
    `upload_date` DATETIME NOT NULL,
    `data` LONGBLOB NOT NULL COMMENT 'Encrypted file binary data',
    `patient_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_document_patient`
        FOREIGN KEY (`patient_id`)
        REFERENCES `patients` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optional: Indexing for faster retrieval in the terminal
CREATE INDEX `idx_document_patient_id` ON `documents` (`patient_id`);
CREATE INDEX `idx_document_category` ON `documents` (`category`);