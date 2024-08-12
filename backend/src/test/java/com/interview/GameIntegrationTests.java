package com.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.repository.*;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Integration tests to cover the basic REST API functionality and validation of the win conditions.
 * Refreshes the Spring context between each test so we have a clean database for each.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GameIntegrationTests {

  @Autowired MockMvc mockMvc;
  @Autowired
  GameGridSquareRepository gameGridSquareRepository;
  @Autowired
  GameLineRepository gameLineRepository;

  @Test
  @Sql("classpath:testData.sql")
  public void getExistingGames() throws Exception {
    // test fetching the first board, also verifies the board print mechanic
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/games/1?showBoard=true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.total_moves").value(8))
        .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$.last_move").value("X"))
        .andExpect(jsonPath("$.board[0]").value("XOX"))
        .andExpect(jsonPath("$.board[1]").value("XOX"))
        .andExpect(jsonPath("$.board[2]").value("OX-"));

    // test same get with showBoard false
    mockMvc
        .perform(MockMvcRequestBuilders.get("/games/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.total_moves").value(8))
        .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$.last_move").value("X"))
        .andExpect(jsonPath("$.board").doesNotExist());

    // test get all
    mockMvc
        .perform(MockMvcRequestBuilders.get("/games").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].total_moves").value(8))
        .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$[0].last_move").value("X"))
        .andExpect(jsonPath("$[0].board").doesNotExist())
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].total_moves").value(5))
        .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$[1].last_move").value("X"))
        .andExpect(jsonPath("$[1].board").doesNotExist());

    // test that we get a 404 when we fetch a non-existent value
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/games/10").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value("NOT_FOUND"))
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.reason").value("Resource not found for path: /games/10"));
  }

  @Test
  @Sql("classpath:testData.sql")
  public void deleteExistingGame() throws Exception {
    // Delete one of the existing games we loaded into the DB before this test
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/games/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // verify that the grids and line objects were successfully deleted
    assertEquals(0, gameGridSquareRepository.countByGameId(1));
    assertEquals(0, gameLineRepository.countByGameId(1));

    // Try deleting it again to make sure it throws a 404
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/games/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value("NOT_FOUND"))
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.reason").value("Resource not found for path: /games/1"));
  }

  @ParameterizedTest
  @ValueSource(ints = {3, 6, 100})
  public void testRowAndColumnWinCondition(int boardSize) throws Exception {
    int rowCondition = createGame(boardSize);
    assertEquals((long) boardSize * boardSize, gameGridSquareRepository.countByGameId(rowCondition));
    assertEquals((long) boardSize * 2, gameLineRepository.countByGameId(rowCondition));

    int colCondition = createGame(boardSize);
    assertEquals((long) boardSize * boardSize, gameGridSquareRepository.countByGameId(colCondition));
    assertEquals((long) boardSize * 2, gameLineRepository.countByGameId(colCondition));

    for (int i = 0; i < boardSize - 1; i++) {
      // In the first game, the players each fill up a row in turn, stopping one from the end
      makeMove(rowCondition, 0, i, "X", "IN_PROGRESS");
      makeMove(rowCondition, 1, i, "O", "IN_PROGRESS");

      // same thing testing the columns
      makeMove(colCondition, i, 0, "X", "IN_PROGRESS");
      makeMove(colCondition, i, 1, "O", "IN_PROGRESS");
    }
    // filling the final row and column should end the games
    makeMove(rowCondition, 0, boardSize - 1, "X", "FINISHED");
    makeMove(colCondition, boardSize - 1, 0, "X", "FINISHED");
  }

  @ParameterizedTest
  @ValueSource(ints = {3, 6, 100})
  public void testBackwardDiagonalWinCondition(int boardSize) throws Exception {
    int gameId = createGame(boardSize);
    assertEquals((long) boardSize * boardSize, gameGridSquareRepository.countByGameId(gameId));
    assertEquals((long) boardSize * 2, gameLineRepository.countByGameId(gameId));

    // Fills the backward diagonal for the first player, second player takes the space to the right
    // Stop before the last spot in the diagonal is filled
    // Moves will look like this (for size 3)...
    // 1 2 -
    // - 3 4
    // - - 5
    for (int i = 0; i < boardSize - 1; i++) {
      makeMove(gameId, i, i, "X", "IN_PROGRESS");
      makeMove(gameId, i, i + 1, "O", "IN_PROGRESS");
    }
    // fill in the final space
    makeMove(gameId, boardSize - 1, boardSize - 1, "X", "FINISHED");
  }

  @ParameterizedTest
  @ValueSource(ints = {3, 6, 100})
  public void testForwardDiagonalWinCondition(int boardSize) throws Exception {
    int gameId = createGame(boardSize);
    assertEquals((long) boardSize * boardSize, gameGridSquareRepository.countByGameId(gameId));
    assertEquals((long) boardSize * 2, gameLineRepository.countByGameId(gameId));

    // Fills the forward diagonal for the first player, second player takes the space above
    // Stop before the last spot in the diagonal is filled
    // Moves will look like this (for size 3)...
    // - 4 5
    // 2 3 -
    // 1 - -
    for (int i = 0; i < boardSize - 1; i++) {
      int row = (boardSize - 1) - i;
      makeMove(gameId, row, i, "X", "IN_PROGRESS");
      makeMove(gameId, row - 1, i, "O", "IN_PROGRESS");
    }
    // fill in the final space
    makeMove(gameId, 0, boardSize - 1, "X", "FINISHED");
  }

  @Test
  @Sql("classpath:testData.sql")
  public void testDrawConditionAndOWinCondition() throws Exception {
    // fill in the last spot on the first preloaded game
    makeMove(1, 2, 2, "O", "DRAW");

    // fill in the winning spot for O on the second preloaded game X wins all the other games in the
    // tests, but the logic is identical for  checking the win condition aside from the value it
    // looks for, so testing for this once should be sufficient to prove all the other win
    // conditions for O will work
    makeMove(2, 2, 2, "O", "FINISHED");
  }

  @Test
  @Sql("classpath:testData.sql")
  public void testPlayerOWins() throws Exception {
    // fill in the last spot on the preloaded game
    makeMove(1, 2, 2, "O", "DRAW");
  }

  @Test
  @Sql("classpath:testData.sql")
  public void testCorruptedGame() throws Exception {
    // Game ID 3 has no grid or count objects loaded, game should end in state ERROR
    makeMove(3, 2, 2, "O", "ERROR");

    //subsequent moves should throw a 422, game cannot continue
    mockMvc
            .perform(
                    MockMvcRequestBuilders.patch("/games/" + 3)
                            .content("{\"row\": 0, \"column\" : 0}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
  }

  private Integer createGame(int size) throws Exception {
    MvcResult createResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/games")
                    .content("{\"board_size\":" + size + "}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.total_moves").value(0))
            .andExpect(jsonPath("$.last_move").value("O"))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.board").doesNotExist())
            .andReturn();

    return JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");
  }

  private void makeMove(int gameId, int row, int col, String expectedPlayer, String expectStatus)
      throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/games/" + gameId)
                .content("{\"row\":" + row + ", \"column\" : " + col + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.last_move").value(expectedPlayer))
        .andExpect(jsonPath("$.status").value(expectStatus));
  }
}
