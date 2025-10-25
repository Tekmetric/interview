package com.interview.service;

import com.interview.model.League;
import com.interview.model.Team;
import com.interview.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The Team Service to do CRUD operations with {@link TeamRepository}
 */
@Service
public class TeamService {

    /**
     * Autowired field by field injection
     * This is included instead of setter injection to show different options for DI
     * Field for Team CRUD operations
     */
    @Autowired
    private TeamRepository teamRepository;

    /**
     * Autowired field by field injection
     * This is included instead of setter injection to show different options for DI
     * Field for League CRUD operations
     */
    @Autowired
    private LeagueService leagueService;

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. Transactional is set to readOnly = true here because we do not need
     * any operation other than READ
     * Function to get all the {@link Team} rows in the Team table
     *
     * @return a List of all the {@link Team} objects in the Team table
     */
    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. Transactional is set to readOnly = true here because we do not need
     * any operation other than READ
     * Function to get a single {@link Team} row in the Team table by id
     *
     * @param id the id of the {@link Team} to get
     * @return a the {@link Team} object that corresponds with the id
     */
    @Transactional(readOnly = true)
    public Optional<Team> getTeam(Long id) {
        return teamRepository.findById(id);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. Transactional is set to readOnly = true here because we do not need
     * any operation other than READ
     * Function to get a single {@link Team} row in the Team table by name
     *
     * @param name the name of the {@link Team} to get
     * @return a the {@link Team} object that corresponds with the name
     */
    @Transactional(readOnly = true)
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful. This is necessary to ensure the league and teams are being created
     * successfully and if any of them fail, this will roll back all the changes.
     * Function to add the {@link Team} into the Team table from the add request.
     * We are checking if the {@link Team} has an associated {@link League} id within the request.
     * If the {@link League} id is available, we will get that object and set the {@link League} for the {@link Team}
     * we are adding. If the {@link League} does not exist, an Exception is thrown.
     * If the {@link League} id is not available, we will create the row anyway with the foreign key as null.
     * This is fine for this scenario as we can update the {@link League} later for the team.
     * (For this I image that a team knows they want to join a league but are unsure of their skill level)
     *
     * @param team the {@link Team} object to create
     * @return the {@link Team} object created (includes {@link League} if the League is available and included in request).
     * @throws Exception if the {@link League} id is included but no {@link League} is associated with that id.
     */
    @Transactional
    public Team addTeam(Team team) throws Exception {
        if (team.getId() != null) {
            team.setId(null);
        }
        if (team.getLeague() != null && team.getLeague().getId() != null) {
            League league = leagueService.getLeague(team.getLeague().getId()).orElseThrow(() -> new Exception("League is not available"));
            team.setLeague(league);
        }
        return teamRepository.save(team);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful.
     * Function to update the whole {@link Team} row in the Team table.
     * We need to check for the {@link League} object. If the object is null, we set leagues to null.
     * If it has just id, we must attempt to retrieve the {@link League} row from the database before adding it to the
     * {@link Team} object. If the {@link League} object is already fully available, we don't need to get anymore data.
     *
     * @param id   the id of the {@link Team} to update
     * @param team the new {@link Team} object to use for updating the {@link Team}
     *             retrieved from the id param
     * @return the {@link Team} of the updated object if the row was found from the id param.
     * Showcasing here and the {@link #partialUpdateTeam(Long, Team)} different ways to deal with
     * Exceptions when trying to get the object to update. Throwing immediately instead of returning
     * {@link Optional} object. We are doing this here because we may have two different scenarios that
     * have missing data. If the {@link League} or {@link Team} is missing we will throw an error.
     */
    @Transactional
    public Team updateTeam(Long id, Team team) throws Exception {
        Team existingTeam = teamRepository.findById(id).orElseThrow(() -> new Exception("Team is not available"));
        existingTeam.setPlayers(team.getPlayers());
        existingTeam.setName(team.getName());
        if (team.getLeague() != null && team.getLeague().getId() != null && team.getLeague().getName() == null) {
            League league = leagueService.getLeague(team.getLeague().getId()).orElseThrow(() -> new Exception("League is not available"));
            existingTeam.setLeague(league);
        } else if (team.getLeague() == null) {
            existingTeam.setLeague(null);
        } else {
            existingTeam.setLeague(team.getLeague());
        }
        return teamRepository.save(existingTeam);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful.
     * Function to partially update the {@link Team} row in the Team table.
     *
     * @param id   the id of the {@link Team} to update
     * @param team the new {@link Team} object to use for updating the {@link Team}
     *             retrieved from the id param. This object may have limited values so we check for null
     *             fields to make sure we are only updating for the data received.
     * @return the {@link Team} of the updated object if the row was found from the id param.
     * @throws Exception if the {@link Team} does not exist in the Team table.
     *                   Showcasing here and the {@link #updateTeam(Long, Team)} to deal with
     *                   Exceptions when trying to get the object to update. Throwing immediately instead of returning
     *                   {@link Optional} object.
     */
    @Transactional
    public Team partialUpdateTeam(Long id, Team team) throws Exception {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new Exception("Team not found when patching"));
        if (team.getPlayers() != null) {
            existingTeam.setPlayers(team.getPlayers());
        }
        if (team.getName() != null) {
            existingTeam.setName(team.getName());
        }
        if (team.getLeague() != null) {
            existingTeam.setLeague(team.getLeague());
        }
        return teamRepository.save(existingTeam);
    }

    /**
     * Transactional to make sure we are committing and flushing at the end of the function
     * if successful.
     * Function to delete the {@link Team} in the Team table from the delete request.
     *
     * @param id the id of the {@link Team} row to delete
     */
    @Transactional
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}
