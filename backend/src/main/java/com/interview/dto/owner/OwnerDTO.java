package com.interview.dto.owner;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.interview.dto.car.CarDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Data transfer object for Owner")
public class OwnerDTO {

  @Schema(description = "Unique identifier of the owner", example = "1")
  private Long id;

  @Schema(description = "Name of the owner", example = "John Doe")
  private String name;

  @Schema(description = "Personal number of the owner", example = "1234567890")
  private String personalNumber;

  @Schema(description = "Address of the owner", example = "123 Main St")
  private String address;

  @Schema(description = "Birth date of the owner", example = "1990-01-01T00:00:00.000Z")
  private Instant birthDate;

  @Schema(description = "Creation timestamp", example = "2024-06-01T12:00:00.000Z")
  private Instant createdAt;

  @Schema(description = "Last update timestamp", example = "2024-06-10T15:30:00.000Z")
  private Instant updatedAt;

  @Schema(description = "List of cars owned by the owner")
  @JsonManagedReference
  private List<CarDTO> cars;
}
