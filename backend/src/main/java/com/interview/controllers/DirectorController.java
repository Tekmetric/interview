package com.interview.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.interview.dtos.ActorDTO;
import com.interview.dtos.DirectorDTO;
import com.interview.models.Actor;
import com.interview.models.Director;
import com.interview.services.DirectorService;

@RequestMapping("/api/director")
@Controller
public class DirectorController {
    DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public ResponseEntity<Page<DirectorDTO>> getActors(final Pageable pageable) {
        return ResponseEntity.ok(directorService.getDirectors(pageable).map(this::convertToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getActorById(@PathVariable("id") long id) {
        Director director = directorService.getDirectorById(id);

        if (director == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDTO(director));
    }

    @PostMapping
    public ResponseEntity<DirectorDTO> saveMovie(@RequestBody DirectorDTO director) {
        return ResponseEntity.ok(convertToDTO(directorService.saveDirector(new Director(director))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorById(@PathVariable("id") long id) {
        directorService.deleteDirectorById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DirectorDTO> updateMovie(@PathVariable("id") long id, final DirectorDTO actor) {
        return ResponseEntity.ok(convertToDTO(directorService.updateDirector(id, new Director(actor))));
    }

    private DirectorDTO convertToDTO(Director director) {
        return new DirectorDTO(director);
    }
}
