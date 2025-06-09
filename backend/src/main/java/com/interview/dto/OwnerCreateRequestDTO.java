package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OwnerCreateRequestDTO {

  @Schema(description = "Name of the owner", example = "John Doe")
  @NotEmpty(message = "Name cannot be null")
  @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
  private String name;

  @Schema(description = "Personal number of the owner", example = "1234567890")
  @NotEmpty
  @Pattern(regexp = "^[0-9]+$", message = "Personal number must contain only digits")
  private String personalNumber;
}
