package com.interview.resource;

import com.interview.entity.Game;
import com.interview.entity.GameGridSquare;
import com.interview.model.GamePatchDto;
import com.interview.model.GameRequestDto;
import com.interview.model.GameResponseDto;
import com.interview.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tic Tac Toe")
@AllArgsConstructor
@RestController
public class GameResource {

  private GameService gameService;

  @PostMapping(value = "/games")
  @Operation(
      summary = "Create a new game",
      description =
          "Creates a new blank game board based on the boardSize you specify. "
              + "To win the game, one player (O or X) must get all spaces in one column, row or diagonal. "
              + "After game is created, use the PATCH /games/{game_id} endpoint to submit moves.")
  @ResponseStatus(HttpStatus.CREATED)
  public GameResponseDto createGame(
      @Valid @RequestBody GameRequestDto gameRequestDto,
      @RequestParam(defaultValue = "false") boolean showBoard) {
    return toDto(gameService.createGame(toEntity(gameRequestDto)), showBoard);
  }

  @PatchMapping(value = "/games/{game_id}")
  @Operation(
      summary = "Make a move.",
      description =
          "Submit a new move for the specified game. Moves will automatically switch back and forth"
              + " between the two players. Row and column values are both zero-based, so (0, 0) is the"
              + " top left of every board. If a win condition is detected, game status will be set to"
              + " \"FINISHED\". The winner is the last player who moved. If no valid moves remain,"
              + " the status will be \"DRAW\".")
  @ResponseStatus(HttpStatus.OK)
  public GameResponseDto makeMove(
      @PathVariable("game_id") long gameId,
      @Valid @RequestBody GamePatchDto gamePatchDto,
      @RequestParam(defaultValue = "true") boolean showBoard) {
    return toDto(
        gameService.makeMove(gameId, gamePatchDto.getRow(), gamePatchDto.getColumn()), showBoard);
  }

  @GetMapping("/games")
  @Operation(
      summary = "List all existing Games",
      description = "Returns all games regardless of status.")
  @ResponseStatus(HttpStatus.OK)
  public List<GameResponseDto> getAllGames(
      @RequestParam(defaultValue = "false") boolean showBoard) {
    return gameService.getAllGames().stream()
        .map(game -> toDto(game, showBoard))
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/games/{game_id}")
  @Operation(
      summary = "Fetch one game",
      description =
          "Returns the game identified by the given id value. Throws a 404 if the game is not found.")
  @ResponseStatus(HttpStatus.OK)
  public GameResponseDto getGameById(
      @PathVariable("game_id") long gameId,
      @RequestParam(defaultValue = "false") boolean showBoard) {
    return toDto(gameService.getGameById(gameId), showBoard);
  }

  @DeleteMapping(value = "/games/{game_id}")
  @Operation(
      summary = "Delete a game",
      description =
          "Delete the game identified by the given id value. This will remove the game board and "
              + "all record of the game. Throws a 404 if the game is not found.")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGameById(@PathVariable("game_id") long gameId) {
    gameService.deleteGameById(gameId);
  }

  /**
   * Convert a given Game entity object into a GameResponseDto. If showBoard is true, converts the
   * database integer values into "O" and "X" values and generates a list of Strings representing
   * each row for human-readable output.
   *
   * @param game Game entity fetched from the database (assumes all values are valid)
   * @param showBoard Boolean - If true gridSquares will be fetched and calculated
   * @return GameResponseDto
   */
  private GameResponseDto toDto(Game game, boolean showBoard) {
    int boardSize = game.getBoardSize();

    GameResponseDto gameResponseDto = new GameResponseDto();
    gameResponseDto.setId(game.getId());
    gameResponseDto.setBoardSize(boardSize);
    gameResponseDto.setStatus(game.getStatus());
    gameResponseDto.setLastMove(playerValueToChar(game.getLastMove()));
    gameResponseDto.setTotalMoves(game.getTotalMoves());

    // gridSquares is lazy loaded, only fetch it and build the board if requested
    if (showBoard) {
      // convert the board to a set of characters representing the stored database values
      char[][] boardChars = new char[boardSize][boardSize];
      for (GameGridSquare gameGridSquare : game.getGameGridSquares()) {
        boardChars[gameGridSquare.getRow()][gameGridSquare.getColumn()] =
            playerValueToChar(gameGridSquare.getValue());
      }
      // now convert each row (the first dimension) into a string and add it to the board list
      gameResponseDto.setBoard(Arrays.stream(boardChars).map(String::new).toList());
    }

    return gameResponseDto;
  }

  /**
   * Convert a GameRequestDto object into a Game entity. Currently only takes in boardSize but
   * defined as a separate method here in case more input params are added later.
   *
   * @param gameRequestDto GameRequestDto Assumes boardSize is non-null
   * @return Game entity with board size filled in
   */
  private Game toEntity(GameRequestDto gameRequestDto) {
    return Game.builder().boardSize(gameRequestDto.getBoardSize()).build();
  }

  /**
   * Convert the integer values stored in the database into display character values.
   *
   * @param value Assumes values -1, 0 or 1
   * @return Either 'X', 'O' or '-'
   */
  private static char playerValueToChar(int value) {
    return value > 0 ? 'X' : (value < 0 ? 'O' : '-');
  }
}
