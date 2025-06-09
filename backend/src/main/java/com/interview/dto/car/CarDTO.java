package com.interview.dto.car;

import com.interview.dto.owner.OwnerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Data transfer object for Car")
public class CarDTO {

  @Schema(description = "Unique identifier of the car", example = "1")
  private Long id;

  @Schema(description = "The model of the car", example = "BMW X5")
  private String model;

  @Schema(description = "The vehicle identification number", example = "CSA1234567890SA")
  private String vin;

  @Schema(description = "The owner of the car", implementation = OwnerDTO.class)
  private Long ownerId;
}
