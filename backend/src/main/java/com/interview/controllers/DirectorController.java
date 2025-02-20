package com.interview.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interview.dto.DirectorDTO;
import com.interview.models.Director;
import com.interview.services.DirectorService;
import com.interview.util.ConvertUtil;

@RequestMapping("/api/director")
@RestController
public class DirectorController {
    DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public ResponseEntity<Page<DirectorDTO>> getActors(final Pageable pageable) {
        return ResponseEntity.ok(directorService.getDirectors(pageable)
                .map(director -> ConvertUtil.convertToDTO(director, DirectorDTO.class)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getActorById(@PathVariable("id") long id) {
        Director director = directorService.getDirectorById(id);
        return ResponseEntity.ok(ConvertUtil.convertToDTO(director, DirectorDTO.class));
    }

    @PostMapping
    public ResponseEntity<DirectorDTO> saveMovie(@RequestBody DirectorDTO director) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ConvertUtil.convertToDTO(directorService.saveDirector(new Director(director)),
                        DirectorDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorById(@PathVariable("id") long id) {
        directorService.deleteDirectorById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DirectorDTO> updateMovie(@PathVariable("id") long id, @RequestBody final DirectorDTO actor) {
        return ResponseEntity.ok(
                ConvertUtil.convertToDTO(directorService.updateDirector(id, new Director(actor)), DirectorDTO.class));
    }
}
