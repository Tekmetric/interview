package com.interview.security.controller;

import com.interview.dto.UserResponse;
import com.interview.security.dto.AdminUserRequest;
import com.interview.security.dto.CustomerRegisterRequest;
import com.interview.security.dto.LoginRequest;
import com.interview.security.dto.LoginResponse;
import com.interview.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/admins")
    public ResponseEntity<UserResponse> createAdminUser(@Valid @RequestBody AdminUserRequest request) {
        return new ResponseEntity<>(authService.createAdmin(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
