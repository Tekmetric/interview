package com.interview.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(name = "AuthRequestDTO", description = "Request payload for user authentication")
public class AuthRequestDTO {

  @Schema(description = "Username for authentication", example = "john_doe")
  @NotBlank(message = "Username cannot be blank")
  private String username;

  @Schema(description = "Password for authentication", example = "securePassword123")
  @NotBlank(message = "Password cannot be blank")
  private String password;
}
