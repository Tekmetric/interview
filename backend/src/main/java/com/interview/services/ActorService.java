package com.interview.services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;
import com.interview.models.Actor;
import com.interview.repositories.IActorRepository;

import jakarta.transaction.Transactional;

@Service
public class ActorService {

    IActorRepository actorRepository;

    public ActorService(IActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @Transactional
    @CacheEvict(value = "actorsList", allEntries = true)
    public Actor saveActor(Actor actor) {
        try {
            return this.actorRepository.save(actor);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationException("Actor already exists");
        }

    }

    @Transactional
    @CacheEvict(value = "actorsList", allEntries = true)
    public void deleteActorById(long id) {
        Actor actor = getActorById(id);
        this.actorRepository.delete(actor);
    }

    @Transactional
    @CacheEvict(value = "actorsList", allEntries = true)
    public Actor updateActor(long id, Actor actor) {
        Actor actorToUpdate = getActorById(id);
        actorToUpdate.setFirstName(actor.getFirstName());
        actorToUpdate.setLastName(actor.getLastName());

        return this.actorRepository.save(actorToUpdate);
    }

    public Actor getActorById(long id) {
        return this.actorRepository.findById(id).orElseThrow(() -> new NotFoundException("Actor not found"));
    }

    @Cacheable(value = "actorsList", key = "'page:' + #pageable.pageNumber + '- size:' + #pageable.pageSize")
    public Page<Actor> getActors(Pageable pageable) {
        return this.actorRepository.findAll(pageable);
    }
}
