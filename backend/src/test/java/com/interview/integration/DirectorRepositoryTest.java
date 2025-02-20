package com.interview.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.interview.models.Director;
import com.interview.repositories.IDirectorRepository;

import jakarta.transaction.Transactional;

@DataJpaTest
public class DirectorRepositoryTest {

    @Autowired
    private IDirectorRepository directorRepository;

    @Test
    @Transactional
    public void testSaveAndFindById() {
        Director director = new Director();
        director.setFirstName("New");
        director.setLastName("Director");

        Director savedDirector = directorRepository.save(director);
        Director foundDirector = directorRepository.findById(savedDirector.getId()).orElse(null);

        assertNotNull(foundDirector);
        assertEquals("New", foundDirector.getFirstName());
        assertEquals("Director", foundDirector.getLastName());
    }

    @Test
    @Transactional
    public void testUpdate() {
        Director director = new Director();
        director.setFirstName("Update");
        director.setLastName("Director");

        Director savedDirector = directorRepository.save(director);
        savedDirector.setFirstName("Updated");
        directorRepository.save(savedDirector);

        Director foundDirector = directorRepository.findById(savedDirector.getId()).orElse(null);

        assertNotNull(foundDirector);
        assertEquals("Updated", foundDirector.getFirstName());
    }

    @Test
    @Transactional
    public void testDelete() {
        Director director = new Director();
        director.setFirstName("Delete");
        director.setLastName("Director");

        Director savedDirector = directorRepository.save(director);
        directorRepository.deleteById(savedDirector.getId());
    }

}
