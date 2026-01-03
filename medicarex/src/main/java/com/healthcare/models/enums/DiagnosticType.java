package com.healthcare.models.enums;

public enum DiagnosticType implements ResourceSubType {
    BLOOD_WORK("Blood Work"),
    URINALYSIS("Urinalysis"),
    PATHOLOGY("Pathology");

    private final String dn;

    DiagnosticType(String dn) {
        this.dn = dn;
    }
    public String getDisplayName() {
        return dn;
    }

}
