package com.interview.enums;

import com.interview.controller.exception.InvalidInventoryStatusException;
import lombok.Getter;

@Getter
public enum InventoryStatus {
    AVAILABLE(true), NOT_AVAILABLE(false);

    private final boolean isActive;

    InventoryStatus(boolean isActive) {
        this.isActive = isActive;
    }

    public static InventoryStatus getInventoryStatus(String str) {
        if (str.equals(AVAILABLE.name())) return AVAILABLE;
        else if (str.equals(NOT_AVAILABLE.name())) return NOT_AVAILABLE;
        else throw new InvalidInventoryStatusException("inventory.invalid.status", "Invalid inventory status!");
    }

}
