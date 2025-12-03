package com.interview.resource.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PhoneTypeEnum {
    HOME("home"),
    WORK("work"),
    MOBILE("mobile");

    private final String phoneType;

    PhoneTypeEnum(String phoneType) {
        this.phoneType = phoneType;
    }

    @JsonValue
    public String getValue() {
        return phoneType;
    }

    @JsonCreator
    public static PhoneTypeEnum of(String value) {
        if (null == value) {
            return null;
        }

        for (PhoneTypeEnum item : PhoneTypeEnum.values()) {
            if (value.toLowerCase().equals(item.getValue())) {
                return item;
            }
        }
        //if invalid enum return null
        return null;
    }
}
