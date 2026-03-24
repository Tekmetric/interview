package com.interview.dto;

import com.interview.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthTokenResponse", description = "JWT access token returned after successful authentication")
public record AuthTokenResponse(
        @Schema(description = "JWT access token")
        String accessToken,
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        @Schema(description = "Token lifetime in seconds", example = "900")
        long expiresIn,
        @Schema(description = "Role granted to the authenticated user", example = "VEHICLE_OWNER")
        UserRole role
) {}

