package com.interview.models.db;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GoalStatusEntity {

    NOT_STARTED("NOT_STARTED"),

    IN_PROGRESS("IN_PROGRESS"),

    COMPLETED("COMPLETED"),

    BLOCKED("BLOCKED");

    private String value;

    GoalStatusEntity(String value) {
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
    public static GoalStatusEntity fromValue(String value) {
        for (GoalStatusEntity b : GoalStatusEntity.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }

        //Use OTHER as default
        return NOT_STARTED;
    }
}