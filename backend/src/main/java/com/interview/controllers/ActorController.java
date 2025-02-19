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
import com.interview.models.Actor;
import com.interview.services.ActorService;

@Controller
@RequestMapping("/api/actor")
public class ActorController {

    ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public ResponseEntity<Page<ActorDTO>> getActors(final Pageable pageable) {
        return ResponseEntity.ok(actorService.getActors(pageable).map(this::convertToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(@PathVariable("id") long id) {
        Actor actor = actorService.getActorById(id);

        if (actor == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertToDTO(actor));
    }

    @PostMapping
    public ResponseEntity<ActorDTO> saveMovie(@RequestBody ActorDTO actor) {
        return ResponseEntity.ok(convertToDTO(actorService.saveActor(new Actor(actor))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActorById(@PathVariable("id") long id) {
        actorService.deleteActorById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActorDTO> updateMovie(@PathVariable("id") long id, final ActorDTO actor) {
        return ResponseEntity.ok(convertToDTO(actorService.updateActor(id, new Actor(actor))));
    }

    private ActorDTO convertToDTO(Actor actor) {
        return new ActorDTO(actor);
    }

}
