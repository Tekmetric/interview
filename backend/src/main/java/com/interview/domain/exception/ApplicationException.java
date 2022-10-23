package com.interview.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Generic application exception.
 */
@Getter
@RequiredArgsConstructor
public class ApplicationException extends RuntimeException {
    /** Exception details. */
    private final ErrorDetail errorDetail;
}
