package com.interview.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.interview.Entities.OlympicTeam;
import com.interview.Services.OlympicTeamsService;

@RestController
public class OlympicTeamsController {

  @Autowired
  private final OlympicTeamsService service;

  OlympicTeamsController(OlympicTeamsService service) {
    this.service = service;
  }

  @GetMapping("/api/teams")
  public ResponseEntity<List<OlympicTeam>> GetOlympicTeams() {

    List<OlympicTeam> teams = service.GetAllOlympicTeams();
    if (teams.size() > 0) {
      return new ResponseEntity<>(teams, HttpStatus.OK);
    }
    return new ResponseEntity<>(teams, HttpStatus.NO_CONTENT);

  }

  @GetMapping("/api/teams/{id}")
  public ResponseEntity<OlympicTeam> GetOlympicTeamById(@PathVariable Integer id) {

    OlympicTeam team = service.GetOlympicTeamById(id);
    if (team != null) {
      return new ResponseEntity<>(team, HttpStatus.OK);
    }
    return new ResponseEntity<>(team, HttpStatus.NOT_FOUND);

  }

  @PostMapping("/api/teams")
  public ResponseEntity<OlympicTeam> CreateOlympicTeam(@RequestBody OlympicTeam olympicTeam) {

    OlympicTeam team = service.CreateOlympicTeam(olympicTeam);
    return new ResponseEntity<>(team, HttpStatus.CREATED);
  }

  // PATCH instead of PUT b/c only want to update specific olympic team fields
  @PatchMapping("/api/teams/{id}")
  public ResponseEntity<OlympicTeam> UpdateOlympicTeam(@PathVariable Integer id,
      @RequestBody OlympicTeam updatedOlympicTeam) {

    OlympicTeam team = service.UpdateOlympicTeam(id, updatedOlympicTeam);
    return new ResponseEntity<>(team, HttpStatus.OK);
  }
}