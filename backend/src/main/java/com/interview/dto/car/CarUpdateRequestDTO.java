package com.interview.dto.car;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "CarUpdateRequestDTO", description = "Request payload for updating a new car")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CarUpdateRequestDTO {

  @NotEmpty(message = "Model cannot be empty")
  @Schema(description = "The model of the car", example = "BMW X5")
  @Size(message = "Model cannot exceed 255 characters", max = 255)
  private String model;

  @NotNull
  @Schema(description = "The new owner id of the car")
  private Long ownerId;
}
