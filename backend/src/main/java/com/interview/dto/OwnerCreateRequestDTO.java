package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
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

  @Schema(description = "Address of the owner", example = "Some Street 123, City, Country")
  @NotEmpty(message = "Address cannot be null")
  private String address;

  @Schema(
      description = "The birth date of the owner in ISO 8601 format",
      example = "1990-01-01T00:00:00Z")
  @NotNull(message = "Birth date cannot be null")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
  private Instant birthDate;
}
