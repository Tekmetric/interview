package com.interview.controller;

import com.interview.model.dto.TeamDTO;
import com.interview.model.NotFoundResponse;
import com.interview.model.Team;
import com.interview.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Team Controller for CRUD Operations
 */
@RestController
@RequestMapping("/api/teams")
@Getter
@Tag(name = "Teams", description = "Team API for CRUD operations")
public class TeamController {
    private final NotFoundResponse notFoundResponse = new NotFoundResponse("Team");
    private TeamService teamService;

    /**
     * Autowired field by setter injection
     *
     * @param teamService the {@link TeamService} used to call the {@link com.interview.repository.TeamRepository}
     */
    @Autowired
    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Endpoint to get all rows in the {@link com.interview.repository.TeamRepository} throw the {@link TeamService}
     *
     * @return a ResponseEntity with a list of {@link TeamDTO}s
     */
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Team.class)))})
    @Operation(summary = "Get all Teams",
            description = "Gets all Teams from the Teams table")
    public ResponseEntity<List<TeamDTO>> getAll() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    /**
     * Endpoint to get a single row in the {@link com.interview.repository.TeamRepository} by id
     *
     * @param id the id of the {@link Team} to get
     * @return a ResponseEntity with the corresponding {@link TeamDTO} or {@link NotFoundResponse}
     */
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))})
    @Operation(summary = "Get single Team by id",
            description = "Gets a single team from the Team table by id")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(teamService.getTeam(id));
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to get a single row in the {@link com.interview.repository.TeamRepository} by name
     *
     * @param name the name of the {@link Team} to get
     * @return a ResponseEntity with the corresponding {@link TeamDTO} or {@link NotFoundResponse}
     */
    @GetMapping("/byName/{name}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))})
    @Operation(summary = "Get single Team by name",
            description = "Gets a single team from the Team table by name")
    public ResponseEntity<?> get(@PathVariable String name) {
        try {
            return ResponseEntity.ok(teamService.getTeamByName(name));
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to add a single row in the {@link com.interview.repository.TeamRepository}
     *
     * @param team the {@link Team} object to add to the Team table
     * @return a ResponseEntity with newly created {@link TeamDTO} or an Error message
     */
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response is okay",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )}),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    description = "Invalid Request. Trying to create a new Team with a conflicting row",
                                    example = "Exception occurred while adding league. Team: Team 1. Error: Row already exists with same name and leagueId")
                    ))
    })
    @Operation(summary = "Add Team",
            description = "Adds the team to the Team table.")
    public ResponseEntity<?> save(@RequestBody Team team) {
        try {
            return ResponseEntity.ok(teamService.addTeam(team));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Exception occurred while adding team. Team: " + team.getName() + ". Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint to update the entire {@link Team} row in the database
     *
     * @param id   the id of the {@link Team} object to update
     * @param team the new {@link Team} object to update the existing Team row
     * @return a ResponseEntity with the newly updated {@link TeamDTO} or {@link NotFoundResponse}
     */
    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )})
    @Operation(summary = "Update Team",
            description = "Updates the team to the Teams table.")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Team team) {
        try {
            TeamDTO result = teamService.updateTeam(id, team);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to partially update {@link Team} row in the database
     *
     * @param id   the id of the {@link Team} object to update
     * @param team the new {@link Team} object to update the existing Team row.
     *             This will have only partial data to update compared to the Update function
     * @return a ResponseEntity with the newly updated {@link TeamDTO} or {@link NotFoundResponse}
     */
    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )})
    @Operation(summary = "Partial Update Team",
            description = "Partially Updates the team to the Team table.")
    public ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody Team team) {
        try {
            return ResponseEntity.ok(teamService.partialUpdateTeam(id, team));
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to remove a row in the {@link com.interview.repository.TeamRepository}
     *
     * @param id the id of the {@link Team} object to delete
     * @return a ResponseEntity with a body of Success or {@link NotFoundResponse}.
     */
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )})
    @Operation(summary = "Delete Team",
            description = "Deletes the team in the Team table.")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok("Successfully deleted row from Team table.");
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }
}
