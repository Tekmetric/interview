package com.interview.controller;

import com.interview.dto.TeamDto;
import com.interview.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("")
    private List<TeamDto> getAll() {
        return teamService.getAll();
    }

    @GetMapping("/{id}")
    private TeamDto getById(@PathVariable("id") Long id) {
        Optional<TeamDto> retVal = teamService.getById(id);

        if(retVal.isEmpty()) {
            throwTeamNotFound(id);
        }

        return retVal.get();
    }

    @PostMapping("")
    private TeamDto create(@Valid @RequestBody TeamDto teamDto) {
        return teamService.create(teamDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteById(@PathVariable("id") Long id) {
        teamService.deleteById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void patchById(@PathVariable("id") Long id, @RequestBody TeamDto partialDto) {
        boolean success = teamService.patchById(id, partialDto);
        if(!success) {
            throwTeamNotFound(id);
        }
    }

    private void throwTeamNotFound(Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No team with id %s found", id));
    }
}
