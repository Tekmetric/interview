package com.interview.dto.car;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "CarCreateRequestDTO", description = "Request payload for creating a new car")
@Data
public class CarCreateRequestDTO {

  private String model;

  private String vin;

  private Long ownerId;
}
