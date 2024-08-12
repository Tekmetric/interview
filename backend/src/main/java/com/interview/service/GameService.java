package com.interview.service;

import static com.interview.config.GameConstants.*;

import com.interview.entity.*;
import com.interview.exception.*;
import com.interview.config.GameStatus;
import com.interview.repository.*;
import jakarta.transaction.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GameService {

  private GameRepository gameRepository;
  private GameGridSquareRepository gameGridSquareRepository;
  private GameLineRepository gameLineRepository;

  public Game getGameById(Long gameId) {
    return gameRepository
        .findById(gameId)
        .orElseThrow(
            () ->
                new GameRuntimeException(
                    String.format(PATH_NOT_FOUND, "/games/" + gameId), HttpStatus.NOT_FOUND));
  }

  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }

  @Transactional
  public void deleteGameById(Long gameId) {
    gameRepository.delete(getGameById(gameId));
  }

  /**
   * Create the baseline game object, setting all the starting game values and filling in the board
   * with blank values by creating game_grid_square objects for every square based on the given
   * board_size. Also creates line objects for every row and column, and sets the diagonal
   *
   * @return Game The created game object, GridSquares are only fetched if needed
   */
  @Transactional
  public Game createGame(Game newGame) {

    // Set starting game values
    newGame.setStatus(GameStatus.IN_PROGRESS);
    newGame.setLastMove(-1);
    newGame.setTotalMoves(0);
    newGame.setForwardDiagSum(0);
    newGame.setBackwardDiagSum(0);

    // Generate the game board, setting the value of all positions to zero
    List<GameGridSquare> newGridList = new ArrayList<>();
    List<GameLine> gameLineList = new ArrayList<>(newGame.getBoardSize() * 2);
    for (int row = 0; row < newGame.getBoardSize(); row++) {
      for (int col = 0; col < newGame.getBoardSize(); col++) {
        newGridList.add(new GameGridSquare(null, null, row, col, 0));
      }

      // add a row and count entry in the count table for every value from 0 to boardSize - 1
      gameLineList.add(new GameLine(null, null, true, row, 0));
      gameLineList.add(new GameLine(null, null, false, row, 0));
    }
    newGame.setGameGridSquares(newGridList);
    newGame.setGameLines(gameLineList);

    // save the game, cascading create to game grid and line entities
    return gameRepository.save(newGame);
  }

  /**
   * Fetch the game identified by the given ID and place the next move on the grid square identified
   * by the given row and column. Automatically sets the X or O value of the square based on the
   * previous move. Invalid moves will result in a GameRuntimeException. Updates the current sums
   * for row, column and the diagonal lines, checking if any of those counts detect a win condition.
   * If an invalid database state is detected, game is set to ERROR mode and locked out.
   *
   * @param gameId Long Unique key for the game the move should be made in
   * @param row Integer zero-based row identifier
   * @param col Integer zero-based column identifier
   * @return Updated Game object
   */
  @Transactional
  public Game makeMove(Long gameId, int row, int col) {
    Game game = getGameById(gameId);

    // Semantic validation
    if (game.getStatus() == GameStatus.ERROR) {
      throw new GameRuntimeException(DATABASE_CORRUPTED, HttpStatus.UNPROCESSABLE_ENTITY);
    } else if (game.getStatus() != GameStatus.IN_PROGRESS) {
      throw new GameRuntimeException(GAME_OVER, HttpStatus.UNPROCESSABLE_ENTITY);
    } else if (row >= game.getBoardSize()) {
      throw new GameRuntimeException(INVALID_ROW, HttpStatus.UNPROCESSABLE_ENTITY);
    } else if (col >= game.getBoardSize()) {
      throw new GameRuntimeException(INVALID_COL, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // update game status data
    int currentMoveValue = -game.getLastMove();
    game.setLastMove(currentMoveValue);
    game.setTotalMoves(game.getTotalMoves() + 1);

    try {
      updateGridSquare(game.getId(), row, col, currentMoveValue);

      // Update all row, column, and diagonal sums. If win condition is detected, set game to
      // finished.
      // If not, check for draw condition (total moves is equal to the max number of total moves)
      if (updateCountsAndCheckWinCondition(game, row, col, currentMoveValue)) {
        game.setStatus(GameStatus.FINISHED);
      } else if (game.getTotalMoves() == (game.getBoardSize() * game.getBoardSize())) {
        game.setStatus(GameStatus.DRAW);
      }
    } catch (GameDatabaseCorruptedException e) {
      // if the database was corrupted somehow, set the status to ERROR so the game can't continue
      game.setStatus(GameStatus.ERROR);
    }

    return gameRepository.save(game);
  }

  /**
   * Updates the current row, column and diagonal counts for the given game based on the current
   * move row and column and move value. This method will assume every move results in a successful
   * update of these values, so it will use these rolling counts to check for the win condition. Win
   * condition is met if the magnitude of any line (row, col or diag) is the same as the boardSize.
   * Each line starts with a count of 0, when X goes on a space in that line a 1 is added and a -1
   * is added for O. If a line reaches the boardSize, we know X won, or if the line reaches
   * -boardSize, O won. Since there are only two diagonals for any board size, they are just tracked
   * on the Game object. The rest are in the game_line table.
   *
   * @param game Game The game being manipulated - diagonal count values will be updated if needed
   * @param row Integer Row for the current move
   * @param col Integer Column for the current move
   * @param currentMoveValue Either 1 (X) or -1 (O)
   * @return True if any of the updated counts match the win condition, False if not
   */
  private boolean updateCountsAndCheckWinCondition(
      Game game, int row, int col, int currentMoveValue) {
    // Update the sums for the row and column lines, save the values to check win condition
    int updatedRowCount = updateRowOrColumnCount(game.getId(), true, row, currentMoveValue);
    int updatedColCount = updateRowOrColumnCount(game.getId(), false, col, currentMoveValue);

    // grid square is on the backward diagonal if its row and column values match
    // for example, the 3x3 square these are (0, 0), (1, 1), and (2, 2)
    if (row == col) {
      game.setBackwardDiagSum(game.getBackwardDiagSum() + currentMoveValue);
    }

    // row is on the forward diagonal if its row and column add up to the boardSize - 1
    // for example, the 3x3 square these are (0, 2), (1, 1), and (2, 0)
    if ((row + col) == (game.getBoardSize() - 1)) {
      game.setForwardDiagSum(game.getForwardDiagSum() + currentMoveValue);
    }

    // multiply the board size by current move (1 or -1) so comparison will check for
    // winning count in the right direction (ex. for 3x3 O-player needs to get to -3)
    int targetCount = game.getBoardSize() * (currentMoveValue);
    return updatedRowCount == targetCount
        || updatedColCount == targetCount
        || game.getBackwardDiagSum() == targetCount
        || game.getForwardDiagSum() == targetCount;
  }

  /**
   * Update a row or column entry by adding the current move value to it. This provides a rolling
   * count aggregating the X/O balance in each row. If the entry cannot be fetched for whatever
   * reason, throws GameDatabaseCorruptedException to alert the caller that the game cannot
   * continue.
   */
  private int updateRowOrColumnCount(long gameId, boolean isRow, int lineId, int currentMoveValue) {
    GameLine gameLine =
        gameLineRepository
            .findByGameIdAndIsRowAndLineId(gameId, isRow, lineId)
            .orElseThrow(
                () ->
                    new GameDatabaseCorruptedException(
                        DATABASE_CORRUPTED, HttpStatus.INTERNAL_SERVER_ERROR));

    gameLine.setLineSum(gameLine.getLineSum() + currentMoveValue);
    gameLineRepository.save(gameLine);
    return gameLine.getLineSum();
  }

  /**
   * Update the value of a specific grid square. If the grid space cannot be found,
   * assumes that the row and col values given were valid and throws a
   * GameDatabaseCorruptedException to alert the caller that the game cannot continue. Throws a
   * GameRuntimeException if specified grid square is already taken (has a non-zero value).
   */
  private void updateGridSquare(long gameId, int row, int col, int currentMoveValue) {
    GameGridSquare gameGridSquare =
        gameGridSquareRepository
            .findByGameIdAndRowAndColumn(gameId, row, col)
            .orElseThrow(
                () ->
                    new GameDatabaseCorruptedException(
                        DATABASE_CORRUPTED, HttpStatus.INTERNAL_SERVER_ERROR));

    if (gameGridSquare.getValue() != 0) {
      throw new GameRuntimeException(SPACE_TAKEN, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    gameGridSquare.setValue(currentMoveValue);
    gameGridSquareRepository.save(gameGridSquare);
  }
}
