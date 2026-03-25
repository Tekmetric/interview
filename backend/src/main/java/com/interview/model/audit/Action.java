package com.interview.model.audit;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Action {

    INSERTED("INSERTED"),
    UPDATED("UPDATED"),
    DELETED("DELETED");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
