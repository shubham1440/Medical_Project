package com.healthcare.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ApproveRequestDTO(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime expiryTime,
        String comment,
        Long documentId
//        PermissionType permissionType
) {}
