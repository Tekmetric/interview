package com.interview.enums;

import com.interview.controller.exception.InvalidInventoryStatusException;

public enum InventoryStatus {
    AVAILABLE, NOT_AVAILABLE;

    public static InventoryStatus getInventoryStatus(String str) {
        if (str.equals(AVAILABLE.name())) return AVAILABLE;
        else if (str.equals(NOT_AVAILABLE.name())) return NOT_AVAILABLE;
        else throw new InvalidInventoryStatusException("inventory.invalid.status", "Invalid inventory status!");
    }
}
