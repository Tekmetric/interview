package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerSortDto {
    ID("id"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName");

    private final String databaseFieldName;
}