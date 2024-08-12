package com.interview.entity;

import com.interview.config.GameStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Baseline game entity; stores information about the status of the game. Contains OneToMany links
 * to game_grid_square and game_line tables to allow for cascading save and delete.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game")
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "board_size")
  private Integer boardSize;

  @Column(name = "total_moves")
  private Integer totalMoves;

  @Column(name = "last_move")
  private Integer lastMove;

  @Enumerated(EnumType.STRING)
  private GameStatus status;

  @Column(name = "forward_diagonal_sum", nullable = false)
  private int forwardDiagSum;

  @Column(name = "backward_diagonal_sum", nullable = false)
  private int backwardDiagSum;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id")
  private List<GameGridSquare> gameGridSquares;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id")
  private List<GameLine> gameLines;
}
