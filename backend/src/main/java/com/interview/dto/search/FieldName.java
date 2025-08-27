package com.interview.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FieldName {

    TYPE("type"),
    PRODUCTION_YEAR("productionYear"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String columnName;

}
