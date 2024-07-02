package com.interview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.*;
import com.interview.exception.PlayerServiceException;
import com.interview.repository.PlayerRepository;
import com.interview.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PlayerApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        playerRepository.deleteAll();
    }

    @Test
    public void givenPlayerCoreFields_whenCreatePlayer_thenReturnSavedPlayer() throws Exception {

        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(Collections.emptyList())
            .racquets(Collections.emptyList())
            .tournaments(Collections.emptyList())
            .build();

        ResultActions response = mockMvc.perform(post("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player)));

        response.andDo(print()).
            andExpect(status().isOk())
            .andExpect(jsonPath("$.name",
                is(player.getName())))
            .andExpect(jsonPath("$.rank",
                is(player.getRank())))
            .andExpect(jsonPath("$.birthdate",
                is(player.getBirthdate())))
            .andExpect(jsonPath("$.birthplace",
                is(player.getBirthplace())))
            .andExpect(jsonPath("$.turnedPro",
                is(player.getTurnedPro())))
            .andExpect(jsonPath("$.weight",
                is(player.getWeight())))
            .andExpect(jsonPath("$.height",
                is(player.getHeight())))
            .andExpect(jsonPath("$.coach",
                is(player.getCoach())));

    }

    @Test
    public void givenPlayerCoreFields_whenUpdatePlayer_thenReturnSavedPlayer() throws Exception {

        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(Collections.emptyList())
            .racquets(Collections.emptyList())
            .tournaments(Collections.emptyList())
            .build();

        PlayerDto savedPlayer = playerService.save(player);

        PlayerDto updatePlayer = PlayerDto.builder()
            .id(savedPlayer.getId())
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(72.00)
            .height(188.00)
            .coach("Roger Nadal")
            .stats(StatsDto.builder().aces(4).doubleFaults(3).losses(2).wins(6).tournamentsPlayed(8).build())
            .previousResults(Collections.emptyList())
            .racquets(Collections.emptyList())
            .tournaments(Collections.emptyList())
            .build();

        ResultActions response = mockMvc.perform(post("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatePlayer)));

        response.andDo(print()).
            andExpect(status().isOk())
            .andExpect(jsonPath("$.name",
                is(updatePlayer.getName())))
            .andExpect(jsonPath("$.rank",
                is(updatePlayer.getRank())))
            .andExpect(jsonPath("$.birthdate",
                is(updatePlayer.getBirthdate())))
            .andExpect(jsonPath("$.birthplace",
                is(updatePlayer.getBirthplace())))
            .andExpect(jsonPath("$.turnedPro",
                is(updatePlayer.getTurnedPro())))
            .andExpect(jsonPath("$.weight",
                is(updatePlayer.getWeight())))
            .andExpect(jsonPath("$.height",
                is(updatePlayer.getHeight())))
            .andExpect(jsonPath("$.coach",
                is(updatePlayer.getCoach())));

    }

    @Test
    public void givenPlayerCoreFields_whenCreateDuplicatePlayer_thenReturnUniqueException() throws Exception {
        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(Collections.emptyList())
            .racquets(Collections.emptyList())
            .tournaments(Collections.emptyList())
            .build();

        playerService.save(player);

        ResultActions response = mockMvc.perform(post("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player)));

        response.andDo(print()).
            andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof PlayerServiceException));

    }

    @Test
    public void givenPlayerWithPreviousResults_whenCreatePlayer_thenReturnSavedPlayer() throws Exception {

        List<ScoreDto> previousResults = new ArrayList<>();
        previousResults.add(ScoreDto.builder().opponentName("Rafael Nadal").points(3).opponentPoints(6).build());
        previousResults.add(ScoreDto.builder().opponentName("Carlos Alcaraz").points(1).opponentPoints(6).build());

        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(previousResults)
            .racquets(Collections.emptyList())
            .tournaments(Collections.emptyList())
            .build();

        ResultActions response = mockMvc.perform(post("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player)));

        response.andDo(print()).
            andExpect(status().isOk())
            .andExpect(jsonPath("$.previousResults.size()",
                is(2)))
            .andExpect(jsonPath("$.previousResults[0].opponentName",
                is("Rafael Nadal")))
            .andExpect(jsonPath("$.previousResults[0].opponentPoints",
                is(6)))
            .andExpect(jsonPath("$.previousResults[0].points",
                is(3)))
            .andExpect(jsonPath("$.previousResults[1].opponentName",
                is("Carlos Alcaraz")))
            .andExpect(jsonPath("$.previousResults[1].opponentPoints",
                is(6)))
            .andExpect(jsonPath("$.previousResults[1].points",
                is(1)));

    }

    @Test
    public void givenPlayerWithTournament_whenCreatePlayer_thenReturnSavedPlayer() throws Exception {
        List<TournamentDto> tournaments = new ArrayList<>();
        tournaments.add(TournamentDto.builder()
            .id(1L)
            .name("US Open")
            .city("New York")
            .country("USA")
            .date("26-06-2023")
            .prizeMoney(1500000.00)
            .surface(SurfaceDto.builder().name("HARD").build())
            .build());

        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(Collections.emptyList())
            .racquets(Collections.emptyList())
            .tournaments(tournaments)
            .build();

        ResultActions response = mockMvc.perform(post("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player)));

        response.andDo(print()).
            andExpect(status().isOk())
            .andExpect(jsonPath("$.tournaments.size()",
                is(1)))
            .andExpect(jsonPath("$.tournaments[0].name",
                is("US Open")))
            .andExpect(jsonPath("$.tournaments[0].city",
                is("New York")))
            .andExpect(jsonPath("$.tournaments[0].country",
                is("USA")))
            .andExpect(jsonPath("$.tournaments[0].prizeMoney",
                is(1500000.00)))
            .andExpect(jsonPath("$.tournaments[0].date",
                is("26-06-2023")))
            .andExpect(jsonPath("$.tournaments[0].surface.name",
                is("HARD")));

    }

    @Test
    public void givenPlayerWithRacquets_whenCreatePlayer_thenReturnSavedPlayer() throws Exception {
        List<RacquetDto> racquets = new ArrayList<>();
        racquets.add(RacquetDto.builder().id(1L).brand("Babolat").model("Pure Aero").weight(300).headSize(100).build());
        racquets.add(RacquetDto.builder().id(2L).brand("Head").model("Speed").weight(310).headSize(98).build());

        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(Collections.emptyList())
            .racquets(racquets)
            .tournaments(Collections.emptyList())
            .build();

        ResultActions response = mockMvc.perform(post("/api/players")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player)));

        response.andDo(print()).
            andExpect(status().isOk())
            .andExpect(jsonPath("$.racquets.size()",
                is(2)))
            .andExpect(jsonPath("$.racquets[0].brand",
                is("Babolat")))
            .andExpect(jsonPath("$.racquets[0].model",
                is("Pure Aero")))
            .andExpect(jsonPath("$.racquets[0].weight",
                is(300)))
            .andExpect(jsonPath("$.racquets[0].headSize",
                is(100)))
            .andExpect(jsonPath("$.racquets[1].brand",
                is("Head")))
            .andExpect(jsonPath("$.racquets[1].model",
                is("Speed")))
            .andExpect(jsonPath("$.racquets[1].weight",
                is(310)))
            .andExpect(jsonPath("$.racquets[1].headSize",
                is(98)));
    }

    @Test
    public void givenPlayerId_whenDeletePlayer_thenReturn200() throws Exception {
        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(Collections.emptyList())
            .racquets(Collections.emptyList())
            .tournaments(Collections.emptyList())
            .build();
        PlayerDto savedPlayer = playerService.save(player);

        ResultActions response = mockMvc.perform(delete("/api/players/{id}", savedPlayer.getId())
            .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
            .andDo(print());

        ResultActions playersResponse = mockMvc.perform(get("/api/players")
            .contentType(MediaType.APPLICATION_JSON));

        playersResponse.andExpect(status().isOk())
            .andExpect(jsonPath("$.size()",
                is(0)));
    }

    @Test
    public void givenPlayerId_whenGetOnePlayer_thenReturn200() throws Exception {
        PlayerDto player = PlayerDto.builder()
            .name("Alin")
            .rank(1)
            .birthdate("26-06-1993")
            .birthplace("Romania")
            .turnedPro("26-06-1993")
            .weight(71.00)
            .height(178.00)
            .coach("Roger Federer")
            .stats(StatsDto.builder().aces(1).doubleFaults(1).losses(1).wins(4).tournamentsPlayed(5).build())
            .previousResults(Collections.emptyList())
            .racquets(Collections.emptyList())
            .tournaments(Collections.emptyList())
            .build();
        PlayerDto savedPlayer = playerService.save(player);

        ResultActions response = mockMvc.perform(get("/api/players/{id}", savedPlayer.getId())
            .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print()).
            andExpect(status().isOk())
            .andExpect(jsonPath("$.name",
                is(player.getName())))
            .andExpect(jsonPath("$.rank",
                is(player.getRank())))
            .andExpect(jsonPath("$.birthdate",
                is(player.getBirthdate())))
            .andExpect(jsonPath("$.birthplace",
                is(player.getBirthplace())))
            .andExpect(jsonPath("$.turnedPro",
                is(player.getTurnedPro())))
            .andExpect(jsonPath("$.weight",
                is(player.getWeight())))
            .andExpect(jsonPath("$.height",
                is(player.getHeight())))
            .andExpect(jsonPath("$.coach",
                is(player.getCoach())));
    }
}
