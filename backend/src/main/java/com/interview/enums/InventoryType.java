package com.interview.enums;

import com.interview.controller.exception.InvalidInventoryTypeException;

public enum InventoryType {
    TYPE_A, TYPE_B, TYPE_C;

    public static InventoryType getInventoryType(String str) {
        if (str.equals(TYPE_A.name())) return TYPE_A;
        else if (str.equals(TYPE_B.name())) return TYPE_B;
        else if (str.equals(TYPE_C.name())) return TYPE_C;
        else throw new InvalidInventoryTypeException("inventory.invalid.type", "Invalid inventory type!");
    }
}
