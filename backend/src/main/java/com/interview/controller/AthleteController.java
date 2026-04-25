package com.interview.controller;

import com.interview.controller.dto.AthleteRequest;
import com.interview.model.Athlete;
import com.interview.service.AthleteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
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
    public ResponseEntity<Object> getAthlete(@PathVariable UUID athleteId) {
        Optional<Athlete> athlete = athleteService.getAthlete(athleteId);

        return athlete.<ResponseEntity<Object>>map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<Page<Athlete>> getAthletes(@PageableDefault(size = 25) Pageable pageable) {
        Page<Athlete> athletes = athleteService.getAllAthletes(pageable);

        return new ResponseEntity<>(athletes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Athlete> createAthlete(@Valid @RequestBody AthleteRequest athlete) {
        Athlete newAthlete = athleteService.createAthlete(
                new Athlete(athlete.getFirstName(), athlete.getLastName(), athlete.getPosition(), athlete.getShoots(), athlete.getNumber())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{athleteId}")
                .buildAndExpand(newAthlete.getAthleteId())
                .toUri();

        return ResponseEntity.created(location).body(newAthlete);
    }

    @PutMapping("/{athleteId}")
    public ResponseEntity<Athlete> updateAthlete(@PathVariable UUID athleteId, @Valid @RequestBody AthleteRequest athlete) {
        Athlete updatedAthlete = athleteService.updateAthlete(
                athleteId,
                new Athlete(athlete.getFirstName(), athlete.getLastName(), athlete.getPosition(), athlete.getShoots(), athlete.getNumber())
        );

        return new ResponseEntity<>(updatedAthlete, HttpStatus.OK);
    }

    @DeleteMapping("/{athleteId}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable UUID athleteId) {
        athleteService.deleteAthlete(athleteId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
