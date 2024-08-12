package com.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameRequestDto {

  @NotNull
  @Min(3)
  @Max(100)
  @JsonProperty(value = "board_size")
  @Schema(
      defaultValue = "3",
      description = "Size of the game board - ex. 3 would create a 3x3 board.")
  private Integer boardSize;
}
