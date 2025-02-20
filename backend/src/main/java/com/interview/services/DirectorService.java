package com.interview.services;

import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;
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
        try {
            return directorRepository.save(director);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationException("Director already exists");
        }
    }

    @Transactional
    public void deleteDirectorById(long id) {
        getDirectorById(id);
        directorRepository.deleteById(id);
    }

    @Transactional
    public Director updateDirector(long id, Director director) {
        Director directorToUpdate = getDirectorById(id);
        directorToUpdate.setFirstName(director.getFirstName());
        directorToUpdate.setLastName(director.getLastName());

        return directorToUpdate;

    }

    public Director getDirectorById(long id) {
        return directorRepository.findById(id).orElseThrow(() -> new NotFoundException("Director not found"));
    }

    public Page<Director> getDirectors(Pageable pageable) {
        return directorRepository.findAll(pageable);
    }

}
