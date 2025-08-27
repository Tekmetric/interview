package com.interview.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(10001, "Internal Server Error"),
    EMPTY_SEARCH_CRITERIA(10002, "Request contains empty search criteria"),
    VEHICLE_NOT_FOUND(10003, "Vehicle not found"),
    DUPLICATE_VEHICLE_VIN(10004, "A resource with the given VIN already exists"),
    INVALID_DATA(10005, "Invalid data provided");


    private final int code;
    private final String message;
}
