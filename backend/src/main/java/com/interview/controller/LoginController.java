package com.interview.controller;

import com.interview.api.LoginApi;
import com.interview.dto.LoginRequest;
import com.interview.dto.LoginResponse;
import com.interview.security.JwtHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController implements LoginApi {

    private final AuthenticationManager authenticationManager;
    private final JwtHelper jwtHelper;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user={}", request.username());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String token = jwtHelper.generateToken(request.username());
        log.info("Login successful for user={}", request.username());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
