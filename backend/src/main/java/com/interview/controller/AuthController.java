package com.interview.controller;

import com.interview.dto.user.AuthResponseDto;
import com.interview.dto.user.LoginRequestDto;
import com.interview.dto.user.TokenResponseDto;
import com.interview.exception.BadRequestException;
import com.interview.model.Token;
import com.interview.service.AuthenticationService;
import com.interview.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authorization Rest API",
        description = "Defines endpoints that can be hit only when the user is not logged in. " +
                "It's not secured by default.Rate limiting value is 20 request per min")
public class AuthController {

    private AuthenticationService authenticationService;
    private TokenService tokenService;


    public AuthController(AuthenticationService authenticationService, TokenService tokenService) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok().body(authenticationService.login(loginRequestDto));
    }

    @GetMapping("/access-token")
    public TokenResponseDto refreshAuth() {
        Optional<Token> optionalRefreshToken = authenticationService.getRefreshToken();
        if (optionalRefreshToken.isPresent()) {
            String newAccessToken = authenticationService.createAccessTokenFromRefreshToken(optionalRefreshToken.get().getUser(), optionalRefreshToken.get().getValue());
            return new TokenResponseDto(newAccessToken);
        }
        throw new BadRequestException("tokenExpired");
    }
}
