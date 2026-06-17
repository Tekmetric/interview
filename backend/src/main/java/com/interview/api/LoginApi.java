package com.interview.api;

import com.interview.dto.login.LoginRequest;
import com.interview.dto.login.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

@Tag(name = "Login API", description = "API Login")
public interface LoginApi {

    @Operation(summary = "Login the user", responses = {
            @ApiResponse(responseCode = "200", description = "Login successful/ Token returned"),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid credentials or empty fields sent",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);
}
