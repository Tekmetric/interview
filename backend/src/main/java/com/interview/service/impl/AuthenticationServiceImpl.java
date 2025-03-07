package com.interview.service.impl;

import com.interview.dto.LoginDto;
import com.interview.dto.LoginResponseDto;
import com.interview.dto.RegistrationDto;
import com.interview.dto.UserDto;
import com.interview.entity.User;
import com.interview.exception.EmailAlreadyRegisteredException;
import com.interview.jwt.JwtService;
import com.interview.repository.UserRepository;
import com.interview.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(final UserRepository userRepository,
                                     final AuthenticationManager authenticationManager,
                                     final JwtService jwtService,
                                     final PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto authenticate(LoginDto loginDto) {
        User authenticatedUser = authenticateUser(loginDto);
        if (authenticatedUser == null) {
            logger.warn("Authentication failed for user: {}", loginDto.getUsername());
            return null;
        }
        logger.debug("Authentication is successful: {}", authenticatedUser);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        logger.debug("Returning JWT token.");
        return new LoginResponseDto(
                new UserDto(authenticatedUser),
                jwtToken,
                jwtService.getExpirationTime());
    }

    private User authenticateUser(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );
        return userRepository.findByEmail(loginDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public RegistrationDto register(RegistrationDto registrationDto) {
        Optional<User> user = userRepository.findByEmail(registrationDto.getEmail());
        if (user.isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        registrationDto.setEncodedPassword(passwordEncoder.encode(registrationDto.getPassword()));
        User registeredUser = new User(registrationDto);
        userRepository.save(registeredUser);

        registrationDto.setPassword(null);
        registrationDto.setEncodedPassword(null);
        return registrationDto;
    }
}
