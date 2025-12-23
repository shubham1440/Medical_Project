package com.healthcare.util;

import java.util.regex.Pattern;

public class IdentifierUtil {
    public static boolean isValidMRN(String mrn) {
        return Pattern.matches("^[A-Z0-9]{6,}$", mrn);
    }

    public static String normalizeMRN(String mrn) {
        return mrn == null ? null : mrn.toUpperCase().trim();
    }
}
