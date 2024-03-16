package com.interview.business.controllers.auth.payloads;

public record AuthResponse(
        String id,
        String name,
        String email,
        String token
) {
}
