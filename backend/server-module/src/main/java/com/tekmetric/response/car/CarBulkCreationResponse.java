package com.tekmetric.response.car;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarBulkCreationResponse {
  @Schema(description = "Number of cars successfully created")
  int successCount;

  @Schema(description = "Number of cars that failed validation or persistence")
  int failureCount;

  @Schema(description = "Successfully created cars")
  List<CarResponse> successes;

  @Schema(description = "Failed items with error details")
  List<CarCreationError> failures;
}
