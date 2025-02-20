package com.interview.integration;

import com.interview.models.Actor;
import com.interview.repositories.IActorRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ActorRepositoryTest {

    @Autowired
    private IActorRepository actorRepository;

    @Test
    @Transactional
    public void testSaveAndFindById() {
        Actor actor = new Actor();
        actor.setFirstName("New");
        actor.setLastName("Actor");

        Actor savedActor = actorRepository.save(actor);
        Actor foundActor = actorRepository.findById(savedActor.getId()).orElse(null);

        assertNotNull(foundActor);
        assertEquals("New", foundActor.getFirstName());
        assertEquals("Actor", foundActor.getLastName());
    }

    @Test
    @Transactional
    public void testUpdate() {
        Actor actor = new Actor();
        actor.setFirstName("Update");
        actor.setLastName("Actor");

        Actor savedActor = actorRepository.save(actor);
        savedActor.setFirstName("Updated");
        actorRepository.save(savedActor);

        Actor foundActor = actorRepository.findById(savedActor.getId()).orElse(null);

        assertNotNull(foundActor);
        assertEquals("Updated", foundActor.getFirstName());
    }

    @Test
    @Transactional
    public void testDelete() {
        Actor actor = new Actor();
        actor.setFirstName("Delete");
        actor.setLastName("Actor");

        Actor savedActor = actorRepository.save(actor);
        actorRepository.deleteById(savedActor.getId());

        Actor foundActor = actorRepository.findById(savedActor.getId()).orElse(null);

        assertNull(foundActor);
    }
}
