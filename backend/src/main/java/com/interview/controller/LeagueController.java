package com.interview.controller;

import com.interview.model.League;
import com.interview.model.NotFoundResponse;
import com.interview.service.LeagueService;
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
    public ResponseEntity<?> get(@PathVariable String name) {
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody League league) {
        League result = leagueService.updateLeague(id, league).orElse(null);
        if (result == null) {
            return ResponseEntity.ok(notFoundResponse.getResponse(new Exception()));
        }
        return ResponseEntity.ok(result);
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
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            leagueService.deleteLeague(id);
            return ResponseEntity.ok("Successfully deleted row from League table.");
        } catch (Exception e) {
            return ResponseEntity.ok(notFoundResponse.getResponse(e));
        }
    }
}
