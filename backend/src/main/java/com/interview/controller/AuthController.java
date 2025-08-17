package com.interview.controller;

import com.interview.config.JwtConfig;
import com.interview.dto.Jwt;
import com.interview.dto.JwtResponse;
import com.interview.dto.LoginRequest;
import com.interview.entity.Customer;
import com.interview.service.CustomerService;
import com.interview.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
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

        Jwt accessToken = jwtService.generateAccessToken(customer);
        Jwt refreshToken = jwtService.generateRefreshToken(customer);

        Cookie cookie = new Cookie("refreshToken", refreshToken.toString());
        // Set refreshToken into HttpOnly to prevent client-side JavaScript from accessing the token
        cookie.setHttpOnly(true);
        // browser only automatically sends to the server when hitting the /auth/refresh endpoint
        cookie.setPath("/auth/refresh");
        // cookie expiration
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        // https only
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    // Use POST instead of GET because this api produces a new token
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        Jwt jwt = jwtService.parseToken(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Only create and return a new access token if the customer still exists
        Customer customer = customerService.findCustomerById(jwt.getCustomerId()).orElseThrow();
        Jwt accessToken = jwtService.generateAccessToken(customer);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    // If login/authentication fails return 401 Unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
