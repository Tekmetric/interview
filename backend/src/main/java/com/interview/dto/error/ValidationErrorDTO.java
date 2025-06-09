package com.interview.dto.error;

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
public class ValidationErrorDTO {
  private String message;
  private Instant timestamp;
  private Map<String, String> errors;
}
