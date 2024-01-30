package com.interview.config.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiExceptionResponse {

    private final long timestamp = System.currentTimeMillis();
    private final String code;
    private final String message;
}
