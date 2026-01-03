CREATE TABLE IF NOT EXISTS `documents` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `file_name` VARCHAR(255) NOT NULL,
    `content_type` VARCHAR(100) NOT NULL,
    `file_size` BIGINT NOT NULL,
    `category` VARCHAR(50) DEFAULT 'General',
    `upload_date` DATETIME NOT NULL,
    `data` LONGBLOB NOT NULL,
    `patient_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_document_patient`
        FOREIGN KEY (`patient_id`)
        REFERENCES `patients` (`id`)
        ON DELETE CASCADE,
    -- Index for category is fine, but patient_id index is already handled by the FK
    INDEX `idx_document_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--CREATE INDEX `idx_docs_patient_lookup` ON `documents` (`patient_id`);
--CREATE INDEX `idx_document_category` ON `documents` (`category`);