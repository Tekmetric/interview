package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OwnerCreateRequestDTO {

  @Schema(description = "Name of the owner", example = "John Doe")
  @NotEmpty(message = "Name cannot be null")
  private String name;

  @Schema(description = "Personal number of the owner", example = "1234567890")
  @NotEmpty
  private String personalNumber;
}
