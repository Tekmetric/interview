package com.interview.controller;

import com.interview.dto.AdminUserRequest;
import com.interview.dto.CustomerRegisterRequest;
import com.interview.dto.LoginRequest;
import com.interview.dto.LoginResponse;
import com.interview.dto.UserResponse;
import com.interview.model.User;
import com.interview.repository.UserRepository;
import com.interview.service.JwtService;
import com.interview.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return new ResponseEntity<>(userService.createCustomerUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/admins")
    public ResponseEntity<UserResponse> createAdminUser(@Valid @RequestBody AdminUserRequest request) {
        return new ResponseEntity<>(userService.createAdminUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails.getUsername(), userDetails.getAuthorities());

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .expiresAt(expiresAt)
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
