package com.interview.security.service;

import org.springframework.stereotype.Service;

import com.interview.security.model.User;
import com.interview.security.resource.dto.AuthenticationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationResponseMapper {

    private final JwtService jwtService;

    public AuthenticationResponse map(User user) {
        return AuthenticationResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .token(jwtService.generateToken(user))
                .build();
    }
}
