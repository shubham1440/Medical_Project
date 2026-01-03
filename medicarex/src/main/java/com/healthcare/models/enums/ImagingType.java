package com.healthcare.models.enums;

public enum ImagingType implements ResourceSubType {
    MRI("MRI Scan"),
    CT_SCAN("CT Scan"),
    XRAY("X-Ray");

    private final String dn;

    ImagingType(String dn) {
        this.dn = dn;
    }

    public String getDisplayName() {
        return dn;
    }
}