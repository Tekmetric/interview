package com.interview.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Stores a rolling sum of all values in a specific row or column; used for detecting win
 * conditions. Each game object will have one of these for every row or column based on its board
 * size. Whenever a move is made, the row and column lines corresponding to that grid square have the
 * current move value added to them and this count can be checked to see if the win condition is met.
 * Diagonals are stored in the Game entity directly, since there are only two of them for any board size.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "game_line")
public class GameLine {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "game_id", nullable = false)
  private Long gameId;

  @Column(name = "is_row", updatable = false)
  private boolean isRow;

  @Column(name = "line_id", nullable = false, updatable = false)
  private int lineId;

  @Setter
  @Column(name = "line_sum", nullable = false)
  private int lineSum;
}
