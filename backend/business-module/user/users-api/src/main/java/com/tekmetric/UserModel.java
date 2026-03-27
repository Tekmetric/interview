package com.tekmetric;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserModel {
  @NotNull private UUID id;
  @NotNull @NotBlank private String firstName;
  @NotNull @NotBlank private String lastName;
  private String email;
  private LocalDate birthDate;
}
