package com.tekmetric.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarBulkCreationRequest {
  @Schema(description = "List of cars to create")
  List<CarCreationRequest> cars;
}
