package com.interview;

import com.interview.controller.LeagueController;
import com.interview.controller.TeamController;
import com.interview.exception.ConflictException;
import com.interview.exception.RowNotFoundException;
import com.interview.model.League;
import com.interview.model.Team;
import com.interview.model.dto.TeamDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Added to reset context after each test
public class InterviewTest {
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private LeagueController leagueController;
    @Autowired
    private TeamController teamController;
    @Test
    @WithMockUser
    void leagueControllerTest() {
        League league = new League(null, "League1Test", "Location1", "Skill1", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "testPassword");
        RequestEntity<League> requestEntity = RequestEntity
                .method(HttpMethod.POST, "/api/leagues")
                .headers(headers)
                .body(league);
        ResponseEntity<League> respPost = rest.exchange("/api/leagues", HttpMethod.POST, requestEntity, League.class);
        System.out.println(respPost.toString());
        Assertions.assertTrue(respPost.getStatusCode().isSameCodeAs(HttpStatus.CREATED));
        Assertions.assertNotNull(respPost.getBody());
        League leagueResponse1 = respPost.getBody();
        Assertions.assertEquals(1, leagueResponse1.getId());
        Assertions.assertEquals("League1Test", leagueResponse1.getName());
        Assertions.assertEquals("Location1", leagueResponse1.getLocation());
        Assertions.assertEquals("Skill1", leagueResponse1.getSkillLevel());
        Assertions.assertNull(leagueResponse1.getTeams());

        ResponseEntity<String> respPostAgain = rest.exchange("/api/leagues", HttpMethod.POST, requestEntity, String.class);
        Assertions.assertEquals(HttpStatus.CONFLICT, respPostAgain.getStatusCode());
        Assertions.assertEquals("A conflict has occurred in the database: Row already exists with same name, location, and skill level", respPostAgain.getBody());

        ResponseEntity<List<League>> respGetAll = rest.exchange("/api/leagues", HttpMethod.GET, null, new ParameterizedTypeReference<List<League>>() {
        });
        Assertions.assertTrue(respGetAll.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(respGetAll.getBody());
        League leagueResponse2 = respGetAll.getBody().getFirst();
        Assertions.assertEquals(1, leagueResponse2.getId());
        Assertions.assertEquals("League1Test", leagueResponse2.getName());
        Assertions.assertEquals("Location1", leagueResponse2.getLocation());
        Assertions.assertEquals("Skill1", leagueResponse2.getSkillLevel());
        Assertions.assertTrue(leagueResponse2.getTeams().isEmpty());
        for (League tempLeague: respGetAll.getBody()) {
            System.out.println(tempLeague.toString());
        }

        League leagueUpdate = new League(null, "League1TestUpdate", "Location1Update", "Skill1Update", null);
        RequestEntity<League> requestEntityUpdate = RequestEntity
                .method(HttpMethod.PUT, "/api/leagues/{id}")
                .headers(headers)
                .body(leagueUpdate);
        ResponseEntity<League> respUpdate = rest.exchange("/api/leagues/{id}", HttpMethod.PUT, requestEntityUpdate, new ParameterizedTypeReference<League>() {}, 1);
        System.out.println(respUpdate);
        Assertions.assertTrue(respUpdate.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(respUpdate.getBody());
        League leagueResponseUpdate = respUpdate.getBody();
        Assertions.assertEquals(1, leagueResponseUpdate.getId());
        Assertions.assertEquals("League1TestUpdate", leagueResponseUpdate.getName());
        Assertions.assertEquals("Location1Update", leagueResponseUpdate.getLocation());
        Assertions.assertEquals("Skill1Update", leagueResponseUpdate.getSkillLevel());

        List<Team> teams = new ArrayList<>();
        Team team = new Team();
        team.setName("testTeam");
        team.setPlayers("Kevin Hall");
        teams.add(team);
        League leagueUpdateAgain = new League(null, "League1TestUpdate", "Location1Update", "Skill1Update", teams);
        RequestEntity<League> requestEntityUpdateAgain = RequestEntity
                .method(HttpMethod.PUT, "/api/leagues/{id}")
                .headers(headers)
                .body(leagueUpdateAgain);

        ResponseEntity<String> respUpdateAgain = rest.exchange("/api/leagues/{id}", HttpMethod.PUT, requestEntityUpdateAgain, new ParameterizedTypeReference<>() {}, 1);
        Assertions.assertTrue(respUpdateAgain.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST));
        Assertions.assertEquals("Missing required data: Team id is required for league to be updated", respUpdateAgain.getBody());

        ResponseEntity<List<TeamDTO>> allTeams = rest.exchange("/api/teams", HttpMethod.GET, null, new ParameterizedTypeReference<List<TeamDTO>>() {
        });
        Assertions.assertNotNull(allTeams);

        League leagueSingleGet = rest.getForObject("/api/leagues/{id}", League.class, 1);
        Assertions.assertNotNull(leagueSingleGet);

        League leaguePartialUpdate = new League();
        leaguePartialUpdate.setName("League1PartialUpdate");
        RequestEntity<League> requestEntityPartialUpdate = RequestEntity
                .method(HttpMethod.PATCH, "/api/leagues/{id}")
                .headers(headers)
                .body(leaguePartialUpdate);
        ResponseEntity<League> respPartialUpdate = rest.exchange("/api/leagues/{id}", HttpMethod.PATCH, requestEntityPartialUpdate, new ParameterizedTypeReference<League>() {}, 1);
        System.out.println(respPartialUpdate);
        Assertions.assertTrue(respPartialUpdate.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(respPartialUpdate.getBody());
        League leagueResponsePartialUpdate = respPartialUpdate.getBody();
        Assertions.assertEquals(1, leagueResponsePartialUpdate.getId());
        Assertions.assertEquals("League1PartialUpdate", leagueResponsePartialUpdate.getName());
        Assertions.assertEquals("Location1Update", leagueResponsePartialUpdate.getLocation());

        RequestEntity<Void> requestEntityDelete = RequestEntity
                .method(HttpMethod.DELETE, "/api/leagues/{id}")
                .headers(headers)
                .build();
        rest.exchange("/api/leagues/{id}", HttpMethod.DELETE, requestEntityDelete, Void.class, 1);

        ResponseEntity<List<League>> respGetAll2 = rest.exchange("/api/leagues", HttpMethod.GET, null, new ParameterizedTypeReference<List<League>>() {
        });
        Assertions.assertNotNull(respGetAll2.getBody());
        Assertions.assertTrue(respGetAll2.getBody().isEmpty());

        ResponseEntity<?> respGetByName = rest.exchange("/api/leagues/byName/{name}", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}, "League1");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, respGetByName.getStatusCode());
        Assertions.assertEquals("Row not found: No League Found for name: League1", respGetByName.getBody());

        League leagueUpdateNoRow = new League(null, "League1TestUpdate", "Location1Update", "Skill1Update", null);
        RequestEntity<League> requestEntityUpdateNoRow = RequestEntity
                .method(HttpMethod.PUT, "/api/leagues/{id}")
                .headers(headers)
                .body(leagueUpdateNoRow);

        ResponseEntity<String> respUpdateNoRow = rest.exchange("/api/leagues/{id}", HttpMethod.PUT, requestEntityUpdateNoRow, new ParameterizedTypeReference<>() {}, 4);
        Assertions.assertTrue(respUpdateNoRow.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND));
        Assertions.assertEquals("Row not found: No League found for id: 4", respUpdateNoRow.getBody());

        RequestEntity<League> requestEntityUnauthorized = RequestEntity
                .method(HttpMethod.PUT, "/api/leagues/{id}")
                .body(leagueUpdateNoRow);

        ResponseEntity<String> respUnauthorized = rest.exchange("/api/leagues/{id}", HttpMethod.PUT, requestEntityUnauthorized, new ParameterizedTypeReference<>() {}, 4);
        Assertions.assertTrue(respUnauthorized.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED));

    }

    @Test
    @WithMockUser
    void teamControllerTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "testPassword");
        League league = new League(null, "League1Test", "Location1", "Skill1", null);
        RequestEntity<League> requestEntityLeague = RequestEntity
                .method(HttpMethod.POST, "/api/leagues")
                .headers(headers)
                .body(league);
        ResponseEntity<League> respPostLeague = rest.exchange("/api/leagues", HttpMethod.POST, requestEntityLeague, League.class);
        Assertions.assertTrue(respPostLeague.getStatusCode().isSameCodeAs(HttpStatus.CREATED));
        Assertions.assertNotNull(respPostLeague.getBody());
        League leagueResponse1 = respPostLeague.getBody();
        Assertions.assertEquals(1L, leagueResponse1.getId());
        Assertions.assertEquals("League1Test", leagueResponse1.getName());
        Assertions.assertEquals("Location1", leagueResponse1.getLocation());
        Assertions.assertEquals("Skill1", leagueResponse1.getSkillLevel());
        Assertions.assertNull(leagueResponse1.getTeams());

        Team team = new Team(null, "Team1", "Kevin Hall, Another Person", new League(2L, null, null, null, null));
        try {
            teamController.save(team);
        } catch (RowNotFoundException ex) {
            Assertions.assertEquals("League is not available while adding team for id: 2", ex.getMessage());
        }

        team = new Team(null, "Team1", "Kevin Hall, Another Person", null);
        RequestEntity<Team> requestEntity = RequestEntity
                .method(HttpMethod.POST, "/api/teams")
                .headers(headers)
                .body(team);
        ResponseEntity<TeamDTO> respPost = rest.exchange("/api/teams", HttpMethod.POST, requestEntity, TeamDTO.class);
        Assertions.assertTrue(respPost.getStatusCode().isSameCodeAs(HttpStatus.CREATED));
        Assertions.assertNotNull(respPost.getBody());
        TeamDTO teamResponse1 = respPost.getBody();
        Assertions.assertEquals(1L, teamResponse1.getId());
        Assertions.assertEquals("Team1", teamResponse1.getName());
        Assertions.assertEquals("Kevin Hall, Another Person", teamResponse1.getPlayers());
        Assertions.assertNull(teamResponse1.getLeagueId());

        team = new Team(null, "Team1", "Kevin Hall, Another Person", new League(1L, null, null, null, null));
        Assertions.assertEquals("{name: Team1, players: Kevin Hall, Another Person, leagueId: 1}", team.toString());
        ResponseEntity<?> responseTeamPost = teamController.save(team);
        Assertions.assertTrue(responseTeamPost.getStatusCode().isSameCodeAs(HttpStatus.CREATED));
        Assertions.assertNotNull(responseTeamPost.getBody());
        TeamDTO teamResponse2 = (TeamDTO) responseTeamPost.getBody();
        Assertions.assertEquals(2L, teamResponse2.getId());
        Assertions.assertEquals("Team1", teamResponse2.getName());
        Assertions.assertEquals("Kevin Hall, Another Person", teamResponse2.getPlayers());
        Assertions.assertEquals(1L, teamResponse2.getLeagueId());

        ResponseEntity<List<TeamDTO>> allTeams = rest.exchange("/api/teams", HttpMethod.GET, null, new ParameterizedTypeReference<List<TeamDTO>>() {
        });
        Assertions.assertNotNull(allTeams.getBody());
        Assertions.assertEquals(2, allTeams.getBody().size());
        TeamDTO team1 = allTeams.getBody().getFirst();
        TeamDTO team2 = allTeams.getBody().get(1);
        Assertions.assertEquals(1L, team1.getId());
        Assertions.assertEquals(2L, team2.getId());
        Assertions.assertNull(team1.getLeagueId());
        Assertions.assertEquals(1L, team2.getLeagueId());

        Team teamUpdate = new Team(1L, "Team1Update", "Jake Hall, Allison Hall", null);
        RequestEntity<Team> requestEntityUpdate = RequestEntity
                .method(HttpMethod.PUT, "/api/teams/{id}")
                .headers(headers)
                .body(teamUpdate);
        ResponseEntity<TeamDTO> respUpdate = rest.exchange("/api/teams/{id}", HttpMethod.PUT, requestEntityUpdate, new ParameterizedTypeReference<TeamDTO>() {}, 1);
        System.out.println(respUpdate);
        Assertions.assertTrue(respUpdate.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(respUpdate.getBody());
        TeamDTO teamResponseUpdate = respUpdate.getBody();
        Assertions.assertEquals(1L, teamResponseUpdate.getId());
        Assertions.assertEquals("Team1Update", teamResponseUpdate.getName());
        Assertions.assertEquals("Jake Hall, Allison Hall", teamResponseUpdate.getPlayers());
        Assertions.assertNull(teamResponseUpdate.getLeagueId());

        ResponseEntity<List<TeamDTO>> getTeamsByName = rest.exchange("/api/teams/byName/{name}", HttpMethod.GET, null, new ParameterizedTypeReference<List<TeamDTO>>() {}, "Team1Update");
        Assertions.assertNotNull(getTeamsByName.getBody());
        Assertions.assertEquals(1, getTeamsByName.getBody().size());
        TeamDTO teamByName = getTeamsByName.getBody().getFirst();
        Assertions.assertEquals(1L, teamByName.getId());
        Assertions.assertEquals("Team1Update", teamByName.getName());
        Assertions.assertEquals("Jake Hall, Allison Hall", teamByName.getPlayers());
        Assertions.assertNull(teamByName.getLeagueId());

        Team partialTeamUpdate = new Team(null, null, "Kevin Hall, Allison Hall", null);
        RequestEntity<Team> requestEntityPatch = RequestEntity
                .method(HttpMethod.PATCH, "/api/teams/{id}")
                .headers(headers)
                .body(partialTeamUpdate);
        ResponseEntity<TeamDTO> respPatch = rest.exchange("/api/teams/{id}", HttpMethod.PATCH, requestEntityPatch, new ParameterizedTypeReference<TeamDTO>() {}, 2);
        Assertions.assertTrue(respPatch.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(respPatch.getBody());
        TeamDTO teamResponsePatch = respPatch.getBody();
        Assertions.assertEquals(2L, teamResponsePatch.getId());
        Assertions.assertEquals("Team1", teamResponsePatch.getName());
        Assertions.assertEquals("Kevin Hall, Allison Hall", teamResponsePatch.getPlayers());
        Assertions.assertEquals(1L, teamResponsePatch.getLeagueId());

        TeamDTO teamGetById = rest.getForObject("/api/teams/{id}", TeamDTO.class, 2);
        Assertions.assertNotNull(teamGetById);
        Assertions.assertEquals(2L, teamGetById.getId());
        Assertions.assertEquals("Team1", teamGetById.getName());
        Assertions.assertEquals("Kevin Hall, Allison Hall", teamGetById.getPlayers());
        Assertions.assertEquals(1L, teamGetById.getLeagueId());

        RequestEntity<Void> requestEntityDelete = RequestEntity
                .method(HttpMethod.DELETE, "/api/teams/{id}")
                .headers(headers)
                .build();
        rest.exchange("/api/teams/{id}", HttpMethod.DELETE, requestEntityDelete, Void.class, 1);

        String teamGetError = rest.getForObject("/api/teams/{id}", String.class, 1);
        Assertions.assertEquals("Row not found: No Team Found for id: 1", teamGetError);

        team = new Team(null, "Team1", "Kevin Hall, Another Person", new League(1L, null, null, null, null));
        try {
            teamController.save(team);
        } catch (ConflictException e) {
            Assertions.assertEquals("Row already exists with same name and league while adding team.", e.getMessage());
        }

        team = new Team(null, "Team2", "Kevin Hall, Another Person", new League(1L));
        ResponseEntity<?> responseTeamPostAgain = teamController.save(team);
        Assertions.assertTrue(responseTeamPostAgain.getStatusCode().isSameCodeAs(HttpStatus.CREATED));
        Assertions.assertNotNull(responseTeamPostAgain.getBody());
        TeamDTO teamResponse2Again = (TeamDTO) responseTeamPostAgain.getBody();
        Assertions.assertEquals(3L, teamResponse2Again.getId());
        Assertions.assertEquals("Team2", teamResponse2Again.getName());
        Assertions.assertEquals("Kevin Hall, Another Person", teamResponse2Again.getPlayers());
        Assertions.assertEquals(1L, teamResponse2Again.getLeagueId());

        League leagueSingleGet = rest.getForObject("/api/leagues/{id}", League.class, 1);
        Assertions.assertNotNull(leagueSingleGet);
        Assertions.assertEquals("{id: 1, name: League1Test, location: Location1, skillLevel: Skill1, teams: [{name: Team1, players: Kevin Hall, Allison Hall, leagueId: 1}, {name: Team2, players: Kevin Hall, Another Person, leagueId: 1}]}", leagueSingleGet.toString());
    }
}
