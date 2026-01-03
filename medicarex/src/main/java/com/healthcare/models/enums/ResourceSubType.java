package com.healthcare.models.enums;

public sealed interface ResourceSubType permits DiagnosticType, ImagingType, PrescriptionType {
    String getDisplayName();
}