package com.interview.security.controller;

import com.interview.dto.UserResponse;
import com.interview.security.dto.CreateUserRequest;
import com.interview.security.dto.LoginRequest;
import com.interview.security.dto.LoginResponse;
import com.interview.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints (public)")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register new user", description = "Public endpoint for customer self-registration. Always creates a user with CUSTOMER role.")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Login", description = "Authenticate user and receive JWT token. Use the token in Authorization header for protected endpoints.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
