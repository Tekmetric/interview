package com.interview.dto.car;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "CarCreateRequestDTO", description = "Request payload for creating a new car")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CarCreateRequestDTO {

  @NotEmpty(message = "Model cannot be empty")
  @Schema(description = "The model of the car", example = "BMW X5")
  @Size(message = "Model cannot exceed 255 characters", max = 255)
  private String model;

  @NotEmpty(message = "Vehicle identification number cannot be empty")
  @Schema(description = "The vehicle identification number", example = "CSA1234567890SA")
  @Size(message = "Vehicle identification number exceed 255 characters", max = 255)
  private String vin;

  @NotNull(message = "Owner ID cannot be null")
  private Long ownerId;
}
