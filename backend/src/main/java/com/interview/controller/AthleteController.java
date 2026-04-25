package com.interview.controller;

import com.interview.model.Athlete;
import com.interview.service.AthleteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/athletes")
public class AthleteController {
    private final AthleteService athleteService;

    public AthleteController(AthleteService athleteService) {
        this.athleteService = athleteService;
    }

    @GetMapping("/{athleteId}")
    public ResponseEntity<Object> getAthlete(@Validated @PathVariable UUID athleteId) {
        Optional<Athlete> athlete = athleteService.getAthlete(athleteId);

        return athlete.<ResponseEntity<Object>>map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Athlete>> getAthletes() {
        List<Athlete> athletes = athleteService.getAllAthletes();

        return new ResponseEntity<>(athletes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Athlete> createAthlete(@Validated @RequestBody Athlete athlete) {
        Athlete newAthlete = athleteService.createAthlete(athlete);

        return new ResponseEntity<>(newAthlete, HttpStatus.CREATED);
    }

    @PutMapping("/{athleteId}")
    public ResponseEntity<Athlete> updateAthlete(@Validated @PathVariable UUID athleteId, @Validated @RequestBody Athlete athlete) {
        Athlete updatedAthlete = athleteService.updateAthlete(athleteId, athlete);

        return new ResponseEntity<>(updatedAthlete, HttpStatus.OK);
    }

    @DeleteMapping("/{athleteId}")
    public ResponseEntity<Athlete> deleteAthlete(@Validated @PathVariable UUID athleteId) {
        athleteService.deleteAthlete(athleteId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
