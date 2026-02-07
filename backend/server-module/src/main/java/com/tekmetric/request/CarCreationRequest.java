package com.tekmetric.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarCreationRequest {
  @Schema(
      description = "Car make. Allowed: Toyota, Honda, BMW, Ford, Tesla, Audi",
      example = "Toyota",
      allowableValues = {"Toyota", "Honda", "BMW", "Ford", "Tesla", "Audi"})
  @NotBlank(message = "Make is required")
  private String make;

  @Schema(
      description =
          """
            Car model.
            Valid combinations:
            - Toyota: RAV4, Camry
            - Honda: CR-V, Accord
            - BMW: i7, X5
            - Ford: Escape, F-150
            - Tesla: Y, 3
            - Audi: Q5, A7
            """,
      example = "RAV4",
      allowableValues = {
        "RAV4", "Camry",
        "CR-V", "Accord",
        "i7", "X5",
        "Escape", "F-150",
        "Y", "3",
        "Q5", "A7"
      })
  @NotBlank(message = "Model is required")
  private String model;

  @Schema(description = "Manufacture year as 4-digit string", example = "2021")
  @NotBlank(message = "Year of manufacture is required")
  @Size(max = 4, message = "Year should be presented as 4-digit string")
  private String manufactureYear;

  @Schema(description = "Car color (free text or your own allowed set)", example = "red")
  private String color;
}
