package com.interview.controllers;

import com.interview.dto.JwtResponse;
import com.interview.dto.LoginRequest;
import com.interview.entity.Customer;
import com.interview.service.CustomerService;
import com.interview.service.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerService customerService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request) {
        // Use Spring Security’s AuthenticationManager to conduct authentication (email exists and password match)
        // If authentication fails, a BadCredentialsException exception is thrown
        // This method eventually calls UserService.loadUserByUsername
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        Customer customer = customerService.findCustomerByEmail(request.getEmail()).orElseThrow();

        var token = jwtService.generateToken(customer);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    // If authentication fails return 401 Unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
