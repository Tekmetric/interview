package com.tekmetric.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarUpdateRequest {
  @Schema(description = "Car color (free text or your own allowed set)", example = "red")
  private String color;

  @Schema(
      description = "Optional owner ID. Null for car without owner",
      example = "d0c1879c-3d51-4e5d-9d68-1b5d4bcf91a2",
      nullable = true)
  private UUID ownerId;
}
