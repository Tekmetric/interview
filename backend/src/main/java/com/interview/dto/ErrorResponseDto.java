package com.interview.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDto(
        String message,
        int errorCode) {

}
