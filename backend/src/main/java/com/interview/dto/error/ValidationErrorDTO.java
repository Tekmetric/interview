package com.interview.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(
    name = "ValidationErrorDTO",
    description = "Data transfer object for validation error responses")
public class ValidationErrorDTO {

  @Schema(
      description = "The validation error message",
      example = "We encountered a validation error processing your request")
  private String message;

  @Schema(description = "Timestamp of the error occurrence", example = "2024-06-01T12:00:00.000Z")
  private Instant timestamp;

  @Schema(
      description = "Map of field names to error messages",
      example =
          "{ \"name\": \"Name is required\", \"address\": \"Address must be a valid format\" }")
  private Map<String, String> errors;
}
