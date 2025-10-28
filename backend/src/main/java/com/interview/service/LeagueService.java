package com.interview.service;

import com.interview.model.League;
import com.interview.model.Team;
import com.interview.repository.LeagueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The League Service to do CRUD operations with {@link LeagueRepository}
 */
@Service
public class LeagueService {

    /**
     * Autowired field by constructor injection
     * This is included instead of setter injection to show different options for DI
     * Field for League CRUD operations
     */
    private final LeagueRepository leagueRepository;

    /**
     * Autowired field by constructor injection
     * This is included instead of setter injection to show different options for DI
     * Field for Team CRUD operations.
     */
    private final TeamService teamService;

    /**
     * Constructor for {@link LeagueService}
     * Autowired field by construction injection. Lazy loading {@link TeamService} due to circular dependencies.
     * @param leagueRepository the {@link LeagueRepository} for League CRUD operations
     * @param teamService Lazy loading due to constraints with circular dependencies.The {@link TeamService} for Team CRUD operations
     */
    @Autowired
    public LeagueService (LeagueRepository leagueRepository, @Lazy TeamService teamService) {
        this.leagueRepository = leagueRepository;
        this.teamService = teamService;
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. Transactional is set to readOnly = true here because we do not need
     * any operation other than READ
     * Function to get all the {@link League} rows in the League table
     *
     * @return a List of all the {@link League} objects in the League table
     */
    @Transactional(readOnly = true)
    public List<League> getAllLeagues() {
        return leagueRepository.findAll();
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. Transactional is set to readOnly = true here because we do not need
     * any operation other than READ
     * Function to get a single {@link League} row in the League table by id
     *
     * @param id the id of the {@link League} to get
     * @return a the {@link League} object that corresponds with the id
     */
    @Transactional(readOnly = true)
    public Optional<League> getLeague(Long id) {
        return leagueRepository.findById(id);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. Transactional is set to readOnly = true here because we do not need
     * any operation other than READ
     * Function to get a single {@link League} row in the League table by name
     *
     * @param name the name of the {@link League} to get
     * @return a the {@link League} object that corresponds with the name
     */
    @Transactional(readOnly = true)
    public Optional<League> getLeagueByName(String name) {
        return leagueRepository.findByName(name);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. This is necessary to ensure the league and teams are being created
     * successfully and if any of them fail, this will roll back all the changes.
     * Function to add the {@link League} into the League table from the add request.
     * Checks to see if there is a row in the {@link League} table with the same fields.
     * We are looping through the teams here to add the {@link League} before creating the {@link Team}s.
     * This is necessary because the id of the {@link League} is only available after the row is inserted.
     * If we did not include the newly created {@link League}, we would not have the foreign key set and therefore
     * would be orphaned.
     *
     * @param league the {@link League} object to create (includes a list of {@link Team}s to add as well)
     * @return the {@link League} object created (includes teams that were created).
     * @throws Exception if there is a row with the same  name, location, and skill level in the database
     *          already or if an Exception is thrown from {@link TeamService#updateTeam(Long, Team)}
     */
    @Transactional
    public League addLeague(League league) throws Exception {
        if (sameRowExist(league)) {
            throw new Exception("Row already exists with same name, location, and skill level");
        }
        if (league.getId() != null) {
            league.setId(null);
        }
        League newLeague = leagueRepository.save(league);
        if (league.getTeams() != null) {
            for (Team team : league.getTeams()) {
                team.setLeague(newLeague);
                teamService.updateTeam(team.getId(), team);
            }
        }
        return newLeague;
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful.
     * Function to update the whole {@link League} row in the League table.
     *
     * @param id     the id of the {@link League} to update
     * @param league the new {@link League} object to use for updating the {@link League}
     *               retrieved from the id param
     * @return the {@link League} of the updated object if the row was found from the id param.
     * @throws Exception when the {@link League} is updating the {@Link Team}s but the teams provided do not have id set.
     * Showcasing here and the {@link #partialUpdateLeague(Long, League)} method with different ways to deal with
     * Exceptions when trying to get the object to update. Returning a {@link Optional} object instead of
     * throwing error immediately
     */
    @Transactional
    public Optional<League> updateLeague(Long id, League league) throws Exception {
        if (league.getTeams() != null && !league.getTeams().isEmpty()) {
            for (Team team : league.getTeams()) {
                if (team.getId() == null) {
                    throw new Exception("Team id is required for league to be updated");
                }
            }
        }
        return leagueRepository.findById(id).map(existingLeague -> {
            existingLeague.setName(league.getName());
            existingLeague.setSkillLevel(league.getSkillLevel());
            existingLeague.setLocation(league.getLocation());
            existingLeague.setTeams(league.getTeams());
            return leagueRepository.save(existingLeague);
        });
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful.
     * Function to partially update the {@link League} row in the League table.
     *
     * @param id     the id of the {@link League} to update
     * @param league the new {@link League} object to use for updating the {@link League}
     *               retrieved from the id param. This object may have limited values so we check for null
     *               fields to make sure we are only updating for the data received.
     * @return the {@link League} of the updated object if the row was found from the id param.
     * @throws Exception if the {@link League} does not exist in the League table.
     *                   Showcasing here and the {@link #updateLeague(Long, League)} different ways to deal with
     *                   Exceptions when trying to get the object to update. Throwing immediately instead of returning
     *                   {@link Optional} object.
     */
    @Transactional
    public League partialUpdateLeague(Long id, League league) throws Exception {
        League existingLeague = leagueRepository.findById(id)
                .orElseThrow(() -> new Exception("League not found when patching"));
        if (league.getTeams() != null) {
            existingLeague.setTeams(league.getTeams());
        }
        if (league.getName() != null) {
            existingLeague.setName(league.getName());
        }
        if (league.getLocation() != null) {
            existingLeague.setLocation(league.getLocation());
        }
        if (league.getSkillLevel() != null) {
            existingLeague.setLocation(league.getSkillLevel());
        }
        return leagueRepository.save(existingLeague);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. This is necessary to ensure the league and teams are being deleted/updated
     * successfully and if any of them fail, this will roll back all the changes.
     * Function to delete the {@link League} in the League table from the delete request.
     * We are looping through the teams here to remove the {@link League} before updating the {@link Team}s.
     * This is necessary because the {@link League} will be deleted so we would run into {@link jakarta.persistence.PersistenceException}
     * if we did not remove the deleted {@link League}.
     * This was part of the design as if a {@link League} is removed, the teams could move to a different league
     * instead of their data being deleted all together.
     * This does leave some orphaned {@link Team}s but that is fine for this scenario.
     *
     * @param id the id of the {@link League} row to delete
     * @throws Exception if the {@link League} retrieved with the id does not exist or {@link Team} associated with the league does not exist.
     */
    @Transactional
    public void deleteLeague(Long id) throws Exception {
        League league = leagueRepository.findById(id).orElseThrow(() -> new Exception("League does not exist to delete"));
        for (Team team : league.getTeams()) {
            team.setLeague(null);
            teamService.updateTeam(team.getId(), team);
        }
        leagueRepository.delete(league);
    }

    /**
     * Function to check if a row with the same name, location, and skill level exists.
     * @param league The {@link League} to search with
     * @return true if a row exists with the same field values, false if the row does not exist already.
     */
    public boolean sameRowExist(League league) {
        return leagueRepository.existsByNameAndLocationAndSkillLevel(league.getName(), league.getLocation(), league.getSkillLevel());
    }
}
