package com.interview.service;

import com.interview.model.Athlete;
import com.interview.repository.AthleteRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Service
public class AthleteService {
    private final AthleteRepository athleteRepository;

    public AthleteService(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    public Page<Athlete> getAllAthletes(Pageable pageable) {
        return athleteRepository.findAll(pageable);
    }

    public Optional<Athlete> getAthlete(UUID athleteId) {
        return athleteRepository.findById(athleteId);
    }

    public Athlete createAthlete(Athlete athlete) {
        return athleteRepository.save(athlete);
    }

    public Athlete updateAthlete(UUID athleteId, Athlete athlete) {
        Athlete existingAthlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new AthleteNotFoundException("No Athlete found with id: " + athleteId));

        existingAthlete.setFirstName(athlete.getFirstName());
        existingAthlete.setLastName(athlete.getLastName());
        existingAthlete.setPosition(athlete.getPosition());
        existingAthlete.setShoots(athlete.getShoots());
        existingAthlete.setNumber(athlete.getNumber());

        return athleteRepository.save(existingAthlete);
    }

    public void deleteAthlete(UUID athleteId) {
        athleteRepository.deleteById(athleteId);
    }
}
