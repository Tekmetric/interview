package com.interview.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Account status enum.
 */
@Getter
@AllArgsConstructor
public enum AccountStatusEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    PENDING("PENDING"),
    SUSPENDED("SUSPENDED");

    private final String value;
}

