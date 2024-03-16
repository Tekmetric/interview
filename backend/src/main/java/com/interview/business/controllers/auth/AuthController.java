package com.interview.business.controllers.auth;

import com.interview.business.controllers.auth.payloads.AuthResponse;
import com.interview.business.services.auth.AuthJwtService;
import com.interview.business.services.users.UsersService;
import com.interview.business.services.users.dto.SignInRequest;
import com.interview.business.services.users.dto.SignUpRequest;
import com.interview.core.api.payloads.DataResponse;
import com.interview.core.exception.ApiException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UsersService usersService;
    private final AuthJwtService authJwtService;

    public AuthController(UsersService usersService, AuthJwtService authJwtService) {
        this.usersService = usersService;
        this.authJwtService = authJwtService;
    }

    @PostMapping("/auth/sign_in")
    public DataResponse<AuthResponse> signIn(@Valid @RequestBody SignInRequest payload) {
        final var user = usersService.signIn(payload).orElseThrow(ApiException::badCredentials);

        final var token = authJwtService.createBy(user);

        return new DataResponse<>(new AuthResponse(
                user.id,
                user.name,
                user.email,
                token
        ));
    }

    @PostMapping("/auth/sign_up")
    public DataResponse<AuthResponse> signUp(@Valid @RequestBody SignUpRequest payload) {
        var user = usersService.signUp(payload);

        final var token = authJwtService.createBy(user);

        return new DataResponse<>(new AuthResponse(
                user.id,
                user.name,
                user.email,
                token
        ));
    }
}
