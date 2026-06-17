package com.interview.dto.auth;

import com.interview.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Successful login response with JWT token",
    example = """
    {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "username": "admin",
        "role": "ADMIN",
        "tokenType": "Bearer",
        "expiresIn": 86400
    }
    """
)
public record LoginResponse(
    @Schema(description = "JWT access token")
    String token,

    @Schema(description = "Username of authenticated user")
    String username,

    @Schema(description = "User role for authorization")
    Role role,

    @Schema(description = "Token type - always Bearer for JWT")
    String tokenType,

    @Schema(description = "Token expiration time in seconds")
    Long expiresIn
) {

    public static LoginResponse of(String token, String username, Role role, Long expiresIn) {
        return new LoginResponse(token, username, role, "Bearer", expiresIn);
    }
}