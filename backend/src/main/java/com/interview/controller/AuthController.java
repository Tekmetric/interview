package com.interview.controller;

import com.interview.dto.AccountRequest;
import com.interview.dto.AccountResponse;
import com.interview.dto.LoginRequest;
import com.interview.dto.LoginResponse;
import com.interview.model.Account;
import com.interview.repository.AccountRepository;
import com.interview.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Authentication APIs for login and registration")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                         UserDetailsService userDetailsService,
                         JwtUtil jwtUtil,
                         AccountRepository accountRepository,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        Account account = accountRepository.findByEmailAndDeletedAtIsNull(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LoginResponse response = new LoginResponse(jwt, account.getEmail(),
                                                   account.getFirstName(), account.getLastName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Email already exists or invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AccountRequest accountRequest) {
        try {
            // Check if non-deleted account with email already exists
            if (accountRepository.findByEmailAndDeletedAtIsNull(accountRequest.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
            }

            // Create new account
            Account account = new Account();
            account.setFirstName(accountRequest.getFirstName());
            account.setLastName(accountRequest.getLastName());
            account.setEmail(accountRequest.getEmail());
            account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
            account.setPhone(accountRequest.getPhone());

            Account savedAccount = accountRepository.save(account);

            // Generate JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(savedAccount.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            LoginResponse response = new LoginResponse(jwt, savedAccount.getEmail(),
                                                       savedAccount.getFirstName(), savedAccount.getLastName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }
}