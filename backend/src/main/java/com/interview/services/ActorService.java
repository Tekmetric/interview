package com.interview.services;

import java.time.Instant;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.models.Actor;
import com.interview.repositories.IActorRepository;

@Service
public class ActorService {

    IActorRepository actorRepository;

    public ActorService(IActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @Transactional
    public Actor saveActor(Actor actor) {
        return this.actorRepository.save(actor);
    }

    @Transactional
    public void deleteActorById(long id) {
        Actor actor = getActorById(id);
        this.actorRepository.delete(actor);
    }

    @Transactional
    public Actor updateActor(long id, Actor actor) {
        Actor actorToUpdate = this.actorRepository.findById(id).get();
        actorToUpdate.setFirstName(actor.getFirstName());
        actorToUpdate.setLastName(actor.getLastName());
        actorToUpdate.setUpdatedAt(Instant.now());

        return this.actorRepository.save(actorToUpdate);
    }

    public Actor getActorById(long id) {
        return this.actorRepository.findById(id).orElseThrow(() -> new RuntimeException("Actor not found"));
    }

    public Page<Actor> getActors(Pageable pageable) {
        return this.actorRepository.findAll(pageable);
    }
}
