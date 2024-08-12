package com.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a space on the game board - value for each space can be blank (0), X (1) or O (-1).
 * Each game will have a number of these equal to the board length/width squared.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "game_grid_square")
public class GameGridSquare {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "game_id", updatable = false)
  private Long gameId;

  @Column(name = "grid_row", nullable = false, updatable = false)
  private int row;

  @Column(name = "grid_col", nullable = false, updatable = false)
  private int column;

  @Setter
  @Column(name = "grid_val")
  private Integer value;
}
