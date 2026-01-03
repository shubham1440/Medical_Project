package com.healthcare.dto.request;

import com.healthcare.models.ConsentItem;
import com.healthcare.models.enums.ConsentStatus;

import java.time.LocalDateTime;
import java.util.*;


public record CreateConsentRequestDTO (
        UUID requestId,
        String provider_name,
        Long provider_id,
        ConsentStatus status,
        String reason,
        List<ConsentItem> items,
        LocalDateTime createdAt // ADD THIS FIELD
) {}
