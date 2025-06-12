package com.interview.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(name = "ErrorDTO", description = "Data transfer object for error responses")
public class ErrorDTO {

  @Schema(
      description = "The failure message",
      example = "We encountered an error processing your request")
  private String message;

  @Schema(description = "Timestamp of the error occurrence", example = "2024-06-01T12:00:00.000Z")
  private Instant timestamp;
}
