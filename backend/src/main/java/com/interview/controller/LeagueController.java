package com.interview.controller;

import com.interview.exception.ConflictException;
import com.interview.exception.RowNotFoundException;
import com.interview.model.League;
import com.interview.model.Team;
import com.interview.service.LeagueService;
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
 * League Controller for CRUD operations.
 */
@RestController
@RequestMapping("/api/leagues")
@Getter
@Tag(name = "Leagues", description = "League API for CRUD operations")
public class LeagueController {
    private LeagueService leagueService;

    /**
     * Autowired field by setter injection
     *
     * @param leagueService the {@link LeagueService} used to call the {@link com.interview.repository.LeagueRepository}
     */
    @Autowired
    public void setLeagueService(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    /**
     * Endpoint to get all the rows in the {@link com.interview.repository.LeagueRepository} through the {@link LeagueService}
     *
     * @return a ResponseEntity with a list of {@link League}s
     */
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = League.class)))})
    @Operation(summary = "Get all Leagues",
            description = "Gets all leagues from the League table")
    public ResponseEntity<List<League>> getAll() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    /**
     * Endpoint to get a single row in the {@link com.interview.repository.LeagueRepository} by id
     *
     * @param id the id of the {@link League} to get
     * @return a ResponseEntity with the corresponding {@link League}.
     */
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class))})
    @ApiResponse(responseCode = "404", description = "Row not found for league", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found for league",
                    example = "Row not found: No League Found for id: 2")))
    @Operation(summary = "Get single League by id",
            description = "Gets a single league from the League table by id")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(leagueService.getLeague(id).orElseThrow(() -> new RowNotFoundException("No League Found for id: " + id)));
    }

    /**
     * Endpoint to get a single row in the {@link com.interview.repository.LeagueRepository} by name
     *
     * @param name the name of the {@link League} to get
     * @return a ResponseEntity with the corresponding {@link League}.
     */
    @GetMapping("/byName/{name}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class))})
    @ApiResponse(responseCode = "404", description = "Row not found for league by name", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found for league by name",
                    example = "Row not found: No League Found for name: League2")))
    @Operation(summary = "Get single League by name",
            description = "Gets a single league from the League table by name")
    public ResponseEntity<?> getByName(@PathVariable String name) {
        return ResponseEntity.ok(leagueService.getLeagueByName(name).orElseThrow(() -> new RowNotFoundException("No League Found for name: " + name)));
    }

    /**
     * Endpoint to add a single row in the {@link com.interview.repository.LeagueRepository}
     *
     * @param league the {@link League} object to add to the League table
     * @return a ResponseEntity with newly created {@link League} or an Error message.
     */
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "League created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )}),
            @ApiResponse(responseCode = "404", description = "Row not found for league or team", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            description = "Row not found for league or team",
                            example = "Row not found: Team is not available while updating team for id: 2"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    description = "Conflict. Trying to create a new League with a conflicting row",
                                    example = "A conflict has occurred in the database: Row already exists with same name, location, and skill level")
                    ))
    })
    @Operation(summary = "Add League",
            description = "Adds the league to the League table. If teams are included also add the teams to the Team table")
    public ResponseEntity<?> save(@RequestBody League league) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leagueService.addLeague(league));
    }

    /**
     * Endpoint to update the entire {@link League} row in the database
     *
     * @param id     the id of the {@link League} object to update
     * @param league the new {@link League} object to update the existing League row
     * @return a ResponseEntity with the newly updated {@link League}.
     * Showcasing here using {@link java.util.Optional} for updating the {@link League}.
     * This way does not require exception handling and if the returning object is not a valid {@link League}.
     * The option to use orElse() function vs isPresent()
     */
    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )})
    @ApiResponse(responseCode = "400", description = "Missing Required Data",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            description = "Missing Required Data. Trying to update a League with teams but missing teams id.",
                            example = "Missing required data: Team id is required for league to be updated")
            ))
    @ApiResponse(responseCode = "404", description = "Row not found for league", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found for league",
                    example = "Row not found: No League found for id: 2")))
    @Operation(summary = "Update League",
            description = "Updates the league to the League table. If teams are included also update the teams in the Team table")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody League league) {
            League result = leagueService.updateLeague(id, league).orElse(null);
            if (result == null) {
                throw new RowNotFoundException("No League found for id: " + id);
            }
            return ResponseEntity.ok(result);
    }

    /**
     * Endpoint to partially update {@link League} row in the database
     *
     * @param id     the id of the {@link League} object to update
     * @param league the new {@link League} object to update the existing League row.
     *               This will have only partial data to update compared to the Update function
     * @return a ResponseEntity with the newly updated {@link League}.
     */
    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )})
    @ApiResponse(responseCode = "404", description = "Row not found for league", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found for league",
                    example = "Row not found: League not found when patching for id: 2")))
    @Operation(summary = "Partial Update League",
            description = "Partially Updates the league to the League table. If teams are included also update the teams in the Team table")
    public ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody League league) {
        return ResponseEntity.ok(leagueService.partialUpdateLeague(id, league));
    }

    /**
     * Endpoint to remove a row in the {@link com.interview.repository.LeagueRepository}
     *
     * @param id the id of the {@link League} object to delete
     * @return a ResponseEntity with a body of Success..
     */
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )})
    @ApiResponse(responseCode = "404", description = "Row not found for league or team", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    description = "Row not found for league or team",
                    example = "Row not found: League does not exist to delete for id: 2")))
    @Operation(summary = "Delete League",
            description = "Deletes the league in the League table. Teams can be orphaned here.")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        leagueService.deleteLeague(id);
        return ResponseEntity.ok("Successfully deleted row from League table.");
    }
}
