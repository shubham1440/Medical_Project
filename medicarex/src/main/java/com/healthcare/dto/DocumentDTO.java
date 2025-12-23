package com.healthcare.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String title;
    private String documentType;       // from DocumentType enum
    private String authorName;
    private String documentDate;       // format: yyyy-MM-dd or ISO string
    private String documentPath;       // link/path to PDF/file/etc (can be null)
    private Integer versionNumber;
    private Boolean isCurrent;
    private String changeSummary;
}
