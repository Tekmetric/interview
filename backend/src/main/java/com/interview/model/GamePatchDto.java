package com.interview.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GamePatchDto {

  @NotNull
  @Min(0)
  @Max(99)
  @Schema(
      defaultValue = "0",
      description =
          "Zero-based identifier of the row for this move.")
  private int row;

  @NotNull
  @Min(0)
  @Max(99)
  @Schema(
      defaultValue = "0",
      description =
          "Zero-based identifier of the column for this move.")
  private int column;
}
