package com.interview.services;

import java.time.Instant;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.models.Director;
import com.interview.repositories.IDirectorRepository;

@Service
public class DirectorService {

    IDirectorRepository directorRepository;

    public DirectorService(IDirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Transactional
    public Director saveDirector(Director director) {
        return directorRepository.save(director);
    }

    @Transactional
    public void deleteDirectorById(long id) {
        directorRepository.deleteById(id);
    }

    @Transactional
    public Director updateDirector(long id, Director director) {
        Director directorToUpdate = getDirectorById(id);
        directorToUpdate.setFirstName(director.getFirstName());
        directorToUpdate.setLastName(director.getLastName());
        directorToUpdate.setUpdatedAt(Instant.now());

        return directorToUpdate;

    }

    public Director getDirectorById(long id) {
        return directorRepository.findById(id).orElseThrow(() -> new RuntimeException("Director not found"));
    }

    public Page<Director> getDirectors(Pageable pageable) {
        return directorRepository.findAll(pageable);
    }

}
