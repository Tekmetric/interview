package com.interview.resource;

import com.interview.dto.ErrorDto;
import com.interview.dto.LoginDto;
import com.interview.dto.LoginResponseDto;
import com.interview.dto.RegistrationDto;
import com.interview.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationResource.class);

    private final AuthenticationService authenticationService;

    public AuthenticationResource(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody @Valid LoginDto loginDto) {
        logger.debug("Authenticating user {}", loginDto.getUsername());
        LoginResponseDto loginResponseDto = authenticationService.authenticate(loginDto);
        if (loginResponseDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> register(@RequestBody @Valid RegistrationDto registrationDto) {
        logger.debug("Registering user {}", registrationDto);
        RegistrationDto registeredUserDto = authenticationService.register(registrationDto);
        if (registeredUserDto == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorDto("Registration failed", "Email already exists!"));
        }
        return ResponseEntity.ok(registeredUserDto);
    }

}


