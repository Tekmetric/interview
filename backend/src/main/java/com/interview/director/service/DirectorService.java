package com.interview.director.service;

import jakarta.transaction.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.director.model.Director;
import com.interview.director.repository.IDirectorRepository;
import com.interview.exceptions.NotFoundException;
import com.interview.exceptions.UniqueConstraintViolationException;

@Service
public class DirectorService {

    IDirectorRepository directorRepository;

    public DirectorService(IDirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Transactional
    @CacheEvict(value = "directorList", allEntries = true)
    public Director saveDirector(Director director) {
        directorRepository.findByFirstNameAndLastName(director.getFirstName(), director.getLastName())
                .ifPresent(m -> {
                    throw new UniqueConstraintViolationException("Director already exists");
                });

        return directorRepository.save(director);

    }

    @Transactional
    @CacheEvict(value = "directorList", allEntries = true)
    public void deleteDirectorById(long id) {
        getDirectorById(id);
        directorRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = "directorList", allEntries = true)
    public Director updateDirector(long id, Director director) {
        Director directorToUpdate = getDirectorById(id);
        directorToUpdate.setFirstName(director.getFirstName());
        directorToUpdate.setLastName(director.getLastName());

        return directorRepository.save(directorToUpdate);

    }

    public Director getDirectorById(long id) {
        return directorRepository.findById(id).orElseThrow(() -> new NotFoundException("Director not found"));
    }

    @Cacheable(value = "directorList", key = "'page:' + #pageable.pageNumber + '- size:' + #pageable.pageSize")
    public Page<Director> getDirectors(Pageable pageable) {
        return directorRepository.findAll(pageable);
    }

}
