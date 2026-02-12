package com.interview.controller;

import com.interview.dto.ErrorResponse;
import com.interview.dto.ValidationErrorResponse;
import com.interview.dto.auth.LoginRequest;
import com.interview.dto.auth.LoginResponse;
import com.interview.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication operations.
 *
 * <p>Provides endpoints for user login and JWT token generation.
 * Supports role-based authentication for ADMIN and USER roles.
 *
 * <p><strong>Demo Credentials:</strong>
 * <ul>
 *   <li>admin/admin123 - ADMIN role (full access)</li>
 *   <li>user/user123 - USER role (read-only access)</li>
 * </ul>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate user and generate JWT token.
     */
    @Operation(summary = "User login", description = "Authenticate user credentials and generate JWT access token")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Login successful",
                                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.username());

        LoginResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }
}