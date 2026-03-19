package com.interview.idempotency;

import java.util.Arrays;

public record IdempotencyRecord(int status, byte[] body, String contentType, long createdAtMillis) {

    public IdempotencyRecord {
        body = body != null ? Arrays.copyOf(body, body.length) : new byte[0];
        contentType = contentType != null ? contentType : "application/json";
    }

    public byte[] body() {
        return Arrays.copyOf(body, body.length);
    }
}
