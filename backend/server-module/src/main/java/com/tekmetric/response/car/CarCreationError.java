package com.tekmetric.response.car;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarCreationError {
  @Schema(description = "Index of the car in the incoming list")
  int index;

  @Schema(
      description = "Reason why this car creation failed",
      example = "Year cannot be in the future")
  String errorMessage;
}
