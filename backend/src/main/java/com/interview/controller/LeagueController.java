package com.interview.controller;

import com.interview.model.League;
import com.interview.model.NotFoundResponse;
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
    private final NotFoundResponse notFoundResponse = new NotFoundResponse("League");
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
     * @return a ResponseEntity with the corresponding {@link League} or {@link NotFoundResponse}
     */
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class))})
    @Operation(summary = "Get single League by id",
            description = "Gets a single league from the League table by id")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(leagueService.getLeague(id).orElseThrow(() -> new Exception("No League Found for id: " + id)));
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to get a single row in the {@link com.interview.repository.LeagueRepository} by name
     *
     * @param name the name of the {@link League} to get
     * @return a ResponseEntity with the corresponding {@link League} or {@link NotFoundResponse}
     */
    @GetMapping("/byName/{name}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class))})
    @Operation(summary = "Get single League by name",
            description = "Gets a single league from the League table by name")
    public ResponseEntity<?> getByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(leagueService.getLeagueByName(name).orElseThrow(() -> new Exception("No League Found for name: " + name)));
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to add a single row in the {@link com.interview.repository.LeagueRepository}
     *
     * @param league the {@link League} object to add to the League table
     * @return a ResponseEntity with newly created {@link League} or an Error message.
     */
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response is okay",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )}),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    description = "Invalid Request. Trying to create a new League with a conflicting row",
                                    example = "Exception occurred while adding league. League: League 1. Error: Row already exists with same name, location, and skill level")
                    ))
    })
    @Operation(summary = "Add League",
            description = "Adds the league to the League table. If teams are included also add the teams to the Team table")
    public ResponseEntity<?> save(@RequestBody League league) {
        try {
            return ResponseEntity.ok(leagueService.addLeague(league));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Exception occurred while adding league. League: " + league.toString() + ". Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint to update the entire {@link League} row in the database
     *
     * @param id     the id of the {@link League} object to update
     * @param league the new {@link League} object to update the existing League row
     * @return a ResponseEntity with the newly updated {@link League} or {@link NotFoundResponse}
     * Showcasing here using {@link java.util.Optional} for updating the {@link League}.
     * This way does not require exception handling and if the returning object is not a valid {@link League}.
     * The option to use orElse() function vs isPresent()
     */
    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )})
    @Operation(summary = "Update League",
            description = "Updates the league to the League table. If teams are included also update the teams in the Team table")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody League league) {
        try {
            League result = leagueService.updateLeague(id, league).orElse(null);
            if (result == null) {
                return ResponseEntity.ok(notFoundResponse.getResponse(new Exception()));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to partially update {@link League} row in the database
     *
     * @param id     the id of the {@link League} object to update
     * @param league the new {@link League} object to update the existing League row.
     *               This will have only partial data to update compared to the Update function
     * @return a ResponseEntity with the newly updated {@link League} or {@link NotFoundResponse}
     */
    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )})
    @Operation(summary = "Partial Update League",
            description = "Partially Updates the league to the League table. If teams are included also update the teams in the Team table")
    public ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody League league) {
        try {
            return ResponseEntity.ok(leagueService.partialUpdateLeague(id, league));
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }

    /**
     * Endpoint to remove a row in the {@link com.interview.repository.LeagueRepository}
     *
     * @param id the id of the {@link League} object to delete
     * @return a ResponseEntity with a body of Success or {@link NotFoundResponse}.
     */
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Response is okay",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = League.class) )})
    @Operation(summary = "Delete League",
            description = "Deletes the league in the League table. Teams can be orphaned here.")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            leagueService.deleteLeague(id);
            return ResponseEntity.ok("Successfully deleted row from League table.");
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }
}
