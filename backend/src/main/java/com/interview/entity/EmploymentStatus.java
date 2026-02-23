package com.interview.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmploymentStatus {
    ACTIVE('A', "Active"),
    TERMINATED('T', "Terminated"),
    ON_LEAVE('L', "On Leave"),
    SUSPENDED('S', "Suspended");

    private final char code;
    private final String displayName;

    EmploymentStatus(char code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public char getCode() {
        return code;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    public static EmploymentStatus fromCode(char code) {
        for (EmploymentStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown employment status code: " + code);
    }

    public static EmploymentStatus fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return fromCode(code.charAt(0));
    }

    @JsonCreator
    public static EmploymentStatus fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String normalized = value.trim();
        for (EmploymentStatus s : values()) {
            if (s.displayName.equalsIgnoreCase(normalized) || s.name().equalsIgnoreCase(normalized)) {
                return s;
            }
        }
        if (normalized.length() == 1) {
            return fromCode(normalized.charAt(0));
        }
        throw new IllegalArgumentException("Unknown employment status: " + value);
    }
}
