package com.interview.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.interview.config.*;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"id", "board_size", "total_moves", "status", "last_move", "board"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameResponseDto {
  private Long id;

  @JsonProperty(value = "board_size")
  private int boardSize;

  private GameStatus status;

  @JsonProperty(value = "total_moves")
  private Integer totalMoves;

  @JsonProperty(value = "last_move")
  private Character lastMove;

  private List<String> board;
}
