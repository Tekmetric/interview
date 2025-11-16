package com.interview.controller;

import com.interview.model.Team;
import com.interview.model.dto.TeamDTO;
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
import org.springframework.http.HttpStatus;
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
     * @return a ResponseEntity with the corresponding {@link TeamDTO}.
     */
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))})
    @ApiResponse(responseCode = "404", description = "No row found for id", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found from id",
                    example = "Row not found: No Team Found for id: 1")))
    @Operation(summary = "Get single Team by id",
            description = "Gets a single team from the Team table by id")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeam(id));
    }

    /**
     * Endpoint to get a single row in the {@link com.interview.repository.TeamRepository} by name
     *
     * @param name the name of the {@link Team} to get
     * @return a ResponseEntity with the corresponding {@link TeamDTO}.
     */
    @GetMapping("/byName/{name}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))})
    @ApiResponse(responseCode = "404", description = "No row found for name", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found from name",
                    example = "Row not found: No Team Found for name: Team1")))
    @Operation(summary = "Get single Team by name",
            description = "Gets a single team from the Team table by name")
    public ResponseEntity<?> get(@PathVariable String name) {
        return ResponseEntity.ok(teamService.getTeamByName(name));
    }

    /**
     * Endpoint to add a single row in the {@link com.interview.repository.TeamRepository}
     *
     * @param team the {@link Team} object to add to the Team table
     * @return a ResponseEntity with newly created {@link TeamDTO} or an Error message
     */
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Team created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )}),
            @ApiResponse(responseCode = "404", description = "Row not found for league", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            description = "Row not found for league",
                            example = "Row not found: League is not available while adding team for id: 2"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    description = "Conflict. Trying to create a new Team with a conflicting row",
                                    example = "A conflict has occurred in the database: Row already exists with same name and league while adding team.")
                    ))
    })
    @Operation(summary = "Add Team",
            description = "Adds the team to the Team table.")
    public ResponseEntity<?> save(@RequestBody Team team) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.addTeam(team));
    }

    /**
     * Endpoint to update the entire {@link Team} row in the database
     *
     * @param id   the id of the {@link Team} object to update
     * @param team the new {@link Team} object to update the existing Team row
     * @return a ResponseEntity with the newly updated {@link TeamDTO}.
     */
    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )})
    @ApiResponse(responseCode = "404", description = "Row not found for league or team", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found for league or team",
                    example = "Row not found: League is not available while updating team for id: 2")))
    @Operation(summary = "Update Team",
            description = "Updates the team to the Teams table.")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Team team) {
        return ResponseEntity.ok(teamService.updateTeam(id, team));
    }

    /**
     * Endpoint to partially update {@link Team} row in the database
     *
     * @param id   the id of the {@link Team} object to update
     * @param team the new {@link Team} object to update the existing Team row.
     *             This will have only partial data to update compared to the Update function
     * @return a ResponseEntity with the newly updated {@link TeamDTO}.
     */
    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )})
    @ApiResponse(responseCode = "404", description = "Row not found for team", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found for team",
                    example = "Row not found: Team is not available for patching team for id: 2")))
    @Operation(summary = "Partial Update Team",
            description = "Partially Updates the team to the Team table.")
    public ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody Team team) {
        return ResponseEntity.ok(teamService.partialUpdateTeam(id, team));
    }

    /**
     * Endpoint to remove a row in the {@link com.interview.repository.TeamRepository}
     *
     * @param id the id of the {@link Team} object to delete
     * @return a ResponseEntity with a body of Success.
     */
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Team.class) )})
    @Operation(summary = "Delete Team",
            description = "Deletes the team in the Team table.")
    public ResponseEntity<?> delete(@PathVariable Long id) {
            teamService.deleteTeam(id);
            return ResponseEntity.ok("Successfully deleted row from Team table.");
    }
}
