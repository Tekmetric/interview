package com.interview.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE('M', "Male"),
    FEMALE('F', "Female"),
    NON_BINARY('N', "Non-Binary"),
    OTHER('O', "Other");

    private final char code;
    private final String displayName;

    Gender(char code, String displayName) {
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

    public static Gender fromCode(char code) {
        for (Gender g : values()) {
            if (g.code == code) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown gender code: " + code);
    }

    public static Gender fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return fromCode(code.charAt(0));
    }

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String normalized = value.trim();
        for (Gender g : values()) {
            if (g.displayName.equalsIgnoreCase(normalized) || g.name().equalsIgnoreCase(normalized)) {
                return g;
            }
        }
        if (normalized.length() == 1) {
            return fromCode(normalized.charAt(0));
        }
        throw new IllegalArgumentException("Unknown gender: " + value);
    }
}
