package com.healthcare.service.Impl;

import com.healthcare.dto.ImportReport;
import com.healthcare.dto.ImportReportRow;
import com.healthcare.models.User;
import com.healthcare.models.enums.Role;
import com.healthcare.repo.UserRepository;
import com.healthcare.service.BulkImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class BulkImportServiceImpl implements BulkImportService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ImportReport importUsersCsv(MultipartFile file) {
        List<ImportReportRow> rows = new ArrayList<>();
        Set<String> emailsInFile = new HashSet<>();

        List<String> allowedRoles = List.of("ADMIN", "PATIENT", "PROVIDER", "OPS");
        List<String> csvLines;
        try {
            csvLines = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines().toList();
        } catch (IOException ex) {
            rows.add(new ImportReportRow(1, "", "FAILED", "Failed to read file: " + ex.getMessage()));
            return new ImportReport(rows);
        }

        int lineNum = 1;
        for (String line : csvLines) {
            if (lineNum == 1) {
                lineNum++;
                continue;
            }

            String[] parts = line.split(",", -1);
            if (parts.length < 4) {
                rows.add(new ImportReportRow(lineNum, "", "FAILED", "Missing required columns"));
                lineNum++;
                continue;
            }

            String email = parts[0].trim();
            String role = parts[1].trim().toUpperCase();
            String firstName = parts[2].trim();
            String lastName = parts[3].trim();
            String phone = (parts.length >= 5) ? parts[4].trim() : "";


            if (email.isEmpty()) {
                rows.add(new ImportReportRow(lineNum, email, "FAILED", "Email is required"));
            } else if (!allowedRoles.contains(role)) {
                rows.add(new ImportReportRow(lineNum, email, "FAILED", "Invalid role: " + role));
            } else if (!emailsInFile.add(email.toLowerCase())) {
                rows.add(new ImportReportRow(lineNum, email, "FAILED", "Duplicate email in file"));
            } else if (userRepository.existsByEmail(email)) {
                rows.add(new ImportReportRow(lineNum, email, "FAILED", "Duplicate email in database"));
            } else {
                try {
                    User user = User.builder()
                            .email(email)
                            .passwordHash(passwordEncoder.encode("defaultPassword"))
                            .firstName(firstName)
                            .lastName(lastName)
                            .phone(phone)
                            .roles(Set.of(Role.valueOf(role)))
                            .accountEnabled(true)
                            .accountLocked(false)
                            .passwordExpired(false)
                            .build();                    userRepository.save(user);
                    rows.add(new ImportReportRow(lineNum, email, "SUCCESS", ""));
                } catch (Exception e) {
                    rows.add(new ImportReportRow(lineNum, email, "FAILED", "Database insert failed: " + e.getMessage()));
                }
            }
            lineNum++;
        }

        return new ImportReport(rows);
    }
}
