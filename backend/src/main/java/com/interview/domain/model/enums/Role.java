package com.interview.domain.model.enums;

import lombok.Getter;

import java.util.HashMap;

@Getter
public enum Role {
    ROLE_SYSTEM_ADMIN("admin"),
    ROLE_USER("user");

    private final String name;
    private static final HashMap<String, Role> namesMap = new HashMap<>(2);

    static {
        namesMap.put(ROLE_SYSTEM_ADMIN.getName(), ROLE_SYSTEM_ADMIN);
        namesMap.put(ROLE_USER.getName(), ROLE_USER);
    }

    Role(String name) {
        this.name = name;
    }

    public static Role forValue(String value) {
        if (value != null) {
            return namesMap.get(value);
        }
        return null;
    }
}
