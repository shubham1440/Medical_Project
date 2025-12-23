package com.healthcare.models;

import com.healthcare.models.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_documents", indexes = {
        @Index(name = "idx_doc_patient", columnList = "patient_id"),
        @Index(name = "idx_doc_type", columnList = "document_type"),
        @Index(name = "idx_doc_date", columnList = "document_date"),
        @Index(name = "idx_doc_path", columnList = "document_path")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Provider author;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "document_date", nullable = false)
    private LocalDateTime documentDate;

    @Column(name = "document_path", length = 500)
    private String documentPath;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber = 1;

    @Column(name = "change_summary", length = 1000)
    private String changeSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_version_id")
    private ClinicalDocument previousVersion;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = true;
}
