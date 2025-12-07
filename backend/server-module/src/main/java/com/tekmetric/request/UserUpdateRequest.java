package com.tekmetric.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
  @Schema(description = "New email for the user", example = "new.email@example.com")
  String email;

  @Schema(
      description =
          "New date of birth. User cannot be older than 120 years and cannot be born in the future.",
      example = "1990-05-12")
  @Past(message = "birthDate must be in the past")
  @JsonFormat(pattern = "yyyy-MM-dd")
  LocalDate birthDate;
}
