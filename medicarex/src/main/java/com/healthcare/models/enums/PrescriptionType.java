package com.healthcare.models.enums;

public enum PrescriptionType implements ResourceSubType {
    CURRENT_MEDS("Current Medications"),
    HISTORY("Prescription History");

    private final String dn;

    PrescriptionType(String dn) {
        this.dn = dn;
    }

    public String getDisplayName() {
        return dn;
    }
}
