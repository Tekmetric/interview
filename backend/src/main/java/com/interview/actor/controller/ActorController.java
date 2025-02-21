package com.interview.actor.controller;

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

import com.interview.actor.dto.ActorDTO;
import com.interview.actor.model.Actor;
import com.interview.actor.service.ActorService;
import com.interview.util.ConvertUtil;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/actor")
public class ActorController {

    ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    /**
     * Get all actors, paged
     * 
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<ActorDTO>> getActors(final Pageable pageable) {
        return ResponseEntity
                .ok(actorService.getActors(pageable).map(actor -> ConvertUtil.convertToDTO(actor, ActorDTO.class)));
    }

    /**
     * Get actor by id
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(@PathVariable("id") long id) {
        Actor actor = actorService.getActorById(id);
        return ResponseEntity.ok(ConvertUtil.convertToDTO(actor, ActorDTO.class));
    }

    /**
     * Create actor
     * 
     * @param actor
     * @return
     */
    @PostMapping
    public ResponseEntity<ActorDTO> createActor(@Valid @RequestBody final ActorDTO actor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ConvertUtil.convertToDTO(actorService.createActor(new Actor(actor)), ActorDTO.class));
    }

    /**
     * Delete actor by id
     * 
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorById(@PathVariable("id") long id) {
        actorService.deleteActorById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update actor
     * 
     * @param id
     * @param actor
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActorDTO> updateActor(@PathVariable("id") long id, @Valid @RequestBody final ActorDTO actor) {
        return ResponseEntity
                .ok(ConvertUtil.convertToDTO(actorService.updateActor(id, new Actor(actor)), ActorDTO.class));
    }

}
