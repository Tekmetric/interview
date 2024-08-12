package com.interview.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interview.Entities.OlympicTeam;
import com.interview.Repositories.OlympicTeamsRepository;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OlympicTeamsService {

    @Autowired
    private OlympicTeamsRepository repo;
    private static final Logger logger = LoggerFactory.getLogger(OlympicTeamsService.class);

    public OlympicTeam GetOlympicTeamById(Integer id) {
        logger.info("retrieving olympic team for id: " + id);
        return repo.findById(id);
    }

    // retrieves all active (non-disabled) olympic teams
    public List<OlympicTeam> GetAllOlympicTeams() {
        logger.info("retrieving all 2024 summer olympic teams");
        return repo.findByDisabledOnIsNotNull();
    }

    public OlympicTeam CreateOlympicTeam(OlympicTeam olympicTeam) {
        logger.info("creating summer olympic team name: " + olympicTeam.teamCountry);
        return repo.save(olympicTeam);
    }

    // updates (and soft-deletes) olympic teams
    public OlympicTeam UpdateOlympicTeam(Integer id, OlympicTeam updatedOlympicTeam) {
        logger.info("updating summer olympic team by ID: " + id);
        OlympicTeam existingTeam = repo.findById(id);

        if (existingTeam == null) {
            throw new EntityNotFoundException("Olympic Team not found");
        }
        existingTeam.setTeamCountry(updatedOlympicTeam.getTeamCountry());
        existingTeam.setTotalAthletes(updatedOlympicTeam.getTotalAthletes());
        existingTeam.setUpdatedOn(updatedOlympicTeam.getUpdatedOn());
        existingTeam.setDisabledOn(updatedOlympicTeam.getDisabledOn());

        return repo.save(existingTeam);
    }
}
