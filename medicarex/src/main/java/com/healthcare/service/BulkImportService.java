package com.healthcare.service;

import com.healthcare.dto.ImportReport;
import org.springframework.web.multipart.MultipartFile;

public interface BulkImportService {

    public ImportReport importUsersCsv(MultipartFile file);
}
