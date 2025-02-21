package com.interview.director.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.interview.director.dto.DirectorDTO;
import com.interview.director.model.Director;
import com.interview.director.service.DirectorService;
import com.interview.shared.util.ConvertUtil;

import jakarta.validation.Valid;

@RequestMapping("/api/director")
@RestController
@Validated
public class DirectorController {
    DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    /**
     * Get all directors, paged
     * 
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<DirectorDTO>> getActors(final Pageable pageable) {
        return ResponseEntity.ok(directorService.getDirectors(pageable)
                .map(director -> ConvertUtil.convertToDTO(director, DirectorDTO.class)));
    }

    /**
     * Get director by id
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getActorById(@PathVariable("id") long id) {
        Director director = directorService.getDirectorById(id);
        return ResponseEntity.ok(ConvertUtil.convertToDTO(director, DirectorDTO.class));
    }

    /**
     * Create director
     * 
     * @param director
     * @return
     */
    @PostMapping
    public ResponseEntity<DirectorDTO> createMovie(@Valid @RequestBody final DirectorDTO director) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ConvertUtil.convertToDTO(directorService.createDirector(new Director(director)),
                        DirectorDTO.class));
    }

    /**
     * Delete director by id
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorById(@PathVariable("id") long id) {
        directorService.deleteDirectorById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update director
     * 
     * @param id
     * @param actor
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<DirectorDTO> updateMovie(@PathVariable("id") long id,
            @Valid @RequestBody final DirectorDTO actor) {
        return ResponseEntity.ok(
                ConvertUtil.convertToDTO(directorService.updateDirector(id, new Director(actor)), DirectorDTO.class));
    }
}
