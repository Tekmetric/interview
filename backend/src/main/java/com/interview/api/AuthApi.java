package com.interview.api;

import com.interview.dto.auth.AuthRequestDTO;
import com.interview.dto.auth.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "Endpoints for user authentication")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/auth")
public interface AuthApi {

  @Operation(
      summary = "Authenticate user and return JWT token",
      requestBody =
          @RequestBody(
              required = true,
              description = "Username and password credentials",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = AuthRequestDTO.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful authentication",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
      })
  @PostMapping("/login")
  ResponseEntity<AuthResponseDTO> login(
      @org.springframework.web.bind.annotation.RequestBody @NotNull @Valid
          final AuthRequestDTO request);
}
