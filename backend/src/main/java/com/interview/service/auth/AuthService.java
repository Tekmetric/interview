package com.interview.service.auth;

import com.interview.dto.auth.LoginRequest;
import com.interview.dto.auth.LoginResponse;
import com.interview.enums.Role;
import com.interview.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication operations.
 *
 * <p>Orchestrates user credential validation and JWT token generation.
 * In production, this would integrate with proper authentication providers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${app.jwt.expiration:86400000}")
    private Long jwtExpiration;

    /**
     * Authenticate user and generate JWT token.
     *
     * @param loginRequest user credentials
     * @return JWT token with user information
     * @throws UnauthorizedException if credentials are invalid
     */
    public LoginResponse authenticate(LoginRequest loginRequest) {
        log.debug("Authenticating user: {}", loginRequest.username());

        // Validate credentials
        if (!userService.validateCredentials(loginRequest.username(), loginRequest.password())) {
            log.warn("Failed authentication attempt for user: {}", loginRequest.username());
            throw UnauthorizedException.invalidCredentials();
        }

        // Get user role
        Role userRole = userService.getUserRole(loginRequest.username())
            .orElseThrow(UnauthorizedException::invalidCredentials);

        // Generate JWT token
        String token = jwtService.generateToken(loginRequest.username(), userRole);

        log.info("Successfully authenticated user: {} with role: {}", loginRequest.username(), userRole);

        return LoginResponse.of(
            token,
            loginRequest.username(),
            userRole,
            jwtExpiration / 1000 // Convert to seconds
        );
    }
}