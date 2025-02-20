package com.interview.actor.repository;

import com.interview.actor.model.Actor;

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
    void testSaveAndFindById() {
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
    void testUpdate() {
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
    void testDelete() {
        Actor actor = new Actor();
        actor.setFirstName("Delete");
        actor.setLastName("Actor");

        Actor savedActor = actorRepository.save(actor);
        actorRepository.deleteById(savedActor.getId());

        Actor foundActor = actorRepository.findById(savedActor.getId()).orElse(null);

        assertNull(foundActor);
    }

    @Test
    @Transactional
    void testFindByFirstNameLastName() {
        Actor actor = new Actor();
        actor.setFirstName("John");
        actor.setLastName("Doe");

        actorRepository.save(actor);

        Actor foundActor = actorRepository.findByFirstNameAndLastName("John", "Doe").orElse(null);

        assertNotNull(foundActor);
        assertEquals("John", foundActor.getFirstName());
        assertEquals("Doe", foundActor.getLastName());
    }
}
