package com.healthcare.dto;

public record ImportReportRow(
        int line,
        String email,
        String status,
        String reason
) {}

