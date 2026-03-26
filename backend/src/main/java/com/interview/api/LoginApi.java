package com.interview.api;

import com.interview.dto.ErrorResponse;
import com.interview.dto.LoginRequest;
import com.interview.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication", description = "Authentication APIs")
public interface LoginApi {

    @Operation(summary = "Login and receive JWT token", description = "Authenticates a user and returns a signed JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(name = "success", value = """
                                    {"token": "eyJhbGciOiJIUzI1NiJ9..."}"""))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "unauthorized", value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "Invalid username or password",
                                      "fieldErrors": null,
                                      "timestamp": "2024-01-01T10:00:00"
                                    }""")))
    })
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request);
}
