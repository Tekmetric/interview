package com.interview.models.db;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EmployeeRoleEntity {

    ENGINEER("ENGINEER"),

    MANAGER("MANAGER"),

    HR("HR"),

    PRODUCT("PRODUCT"),

    OTHER("OTHER");

    private String value;

    EmployeeRoleEntity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EmployeeRoleEntity fromValue(String value) {
        for (EmployeeRoleEntity b : EmployeeRoleEntity.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }

        //Use OTHER as default
        return OTHER;
    }
}