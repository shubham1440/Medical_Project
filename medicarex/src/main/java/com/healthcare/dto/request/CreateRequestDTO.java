package com.healthcare.dto.request;

import com.healthcare.models.ConsentItem;

import java.util.List;

public record CreateRequestDTO(
        Long patientId,
        String reason,
        List<ConsentItem> items
) {}
