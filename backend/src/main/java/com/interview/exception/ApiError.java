package com.interview.exception;

import java.time.Instant;

public record ApiError(
        String title,
        int status,
        String detail,
        String path,
        String traceId,
        Instant timestamp
) {}
