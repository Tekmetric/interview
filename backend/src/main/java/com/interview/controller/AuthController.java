package com.interview.controller;

import com.interview.dto.AdminUserRequest;
import com.interview.dto.CustomerRegisterRequest;
import com.interview.dto.UserResponse;
import com.interview.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return new ResponseEntity<>(userService.createCustomerUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/admins")
    public ResponseEntity<UserResponse> createAdminUser(@Valid @RequestBody AdminUserRequest request) {
        return new ResponseEntity<>(userService.createAdminUser(request), HttpStatus.CREATED);
    }
}
