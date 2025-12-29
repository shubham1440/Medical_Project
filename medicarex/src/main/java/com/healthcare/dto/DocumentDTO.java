package com.healthcare.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String title;          // Maps to fileName in HTML
    private String documentType;   // e.g., application/pdf
    private String authorName;
    private String documentDate;   // format: yyyy-MM-dd
    private String documentPath;
    private byte[] data;           // Base64 or raw string
    private String fileSize;       // Added this to match your UI needs
    private String category;       // Added this to match your UI needs
    private Integer versionNumber;
    private Boolean isCurrent;
    private String changeSummary;

    // Custom constructor for your Mock/Service calls
    public DocumentDTO(Long id, String title, String documentType, String fileSize, String category, String documentDate, byte[] data) {
        this.id = id;
        this.title = title;
        this.documentType = documentType;
        this.fileSize = fileSize;
        this.category = category;
        this.documentDate = documentDate;
        this.data = data;
    }
}