package com.tekmetric.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Error response")
public class ErrorResponse {
  private int status;
  private String message;
  private String path;
  private Instant timestamp;
}
