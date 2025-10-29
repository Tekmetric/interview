package com.interview.controller;

import com.interview.model.Team;
import com.interview.model.dto.TeamDTO;
import com.interview.model.League;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private LeagueController leagueController;
    @Autowired
    private TeamController teamController;
    @Test
    void leagueControllerTest() {
        League league = new League(null, "League1Test", "Location1", "Skill1", null);
        RequestEntity<League> requestEntity = RequestEntity
                .method(HttpMethod.POST, "/api/leagues")
                .body(league);
        ResponseEntity<League> respPost = rest.exchange("/api/leagues", HttpMethod.POST, requestEntity, League.class);
        Assertions.assertTrue(respPost.getStatusCode().isSameCodeAs(HttpStatus.CREATED));
        Assertions.assertNotNull(respPost.getBody());
        League leagueResponse1 = respPost.getBody();
        Assertions.assertEquals(1, respPost.getBody().getId());
        Assertions.assertEquals(1, leagueResponse1.getId());
        Assertions.assertEquals("League1Test", leagueResponse1.getName());
        Assertions.assertEquals("Location1", leagueResponse1.getLocation());
        Assertions.assertEquals("Skill1", leagueResponse1.getSkillLevel());
        Assertions.assertNull(leagueResponse1.getTeams());
        System.out.println(respPost.getBody().toString());

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
                .body(leaguePartialUpdate);
        ResponseEntity<League> respPartialUpdate = rest.exchange("/api/leagues/{id}", HttpMethod.PATCH, requestEntityPartialUpdate, new ParameterizedTypeReference<League>() {}, 1);
        System.out.println(respPartialUpdate);
        Assertions.assertTrue(respPartialUpdate.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(respPartialUpdate.getBody());
        League leagueResponsePartialUpdate = respPartialUpdate.getBody();
        Assertions.assertEquals(1, leagueResponsePartialUpdate.getId());
        Assertions.assertEquals("League1PartialUpdate", leagueResponsePartialUpdate.getName());
        Assertions.assertEquals("Location1Update", leagueResponsePartialUpdate.getLocation());

        rest.delete("/api/leagues/{id}", 1);

        ResponseEntity<List<League>> respGetAll2 = rest.exchange("/api/leagues", HttpMethod.GET, null, new ParameterizedTypeReference<List<League>>() {
        });
        Assertions.assertNotNull(respGetAll2.getBody());
        Assertions.assertTrue(respGetAll2.getBody().isEmpty());

        ResponseEntity<?> respGetByName = rest.exchange("/api/leagues/byName/{name}", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}, "League1");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, respGetByName.getStatusCode());
        Assertions.assertEquals("Row not found: No League Found for name: League1", respGetByName.getBody());
    }
}
