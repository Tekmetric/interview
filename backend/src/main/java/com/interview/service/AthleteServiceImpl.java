package com.interview.service;

import com.interview.exception.AthleteNotFoundException;
import com.interview.exception.DuplicateAthleteException;
import com.interview.model.Athlete;
import com.interview.repository.AthleteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthleteServiceImpl implements AthleteService {

    private final AthleteRepository repository;

    @Override
    public List<Athlete> findAll() {
        log.debug("Fetching all athletes");
        final List<Athlete> athletes = repository.findAll();
        log.info("Retrieved {} athletes", athletes.size());
        return athletes;
    }

    @Override
    public Page<Athlete> findAll(final Specification<Athlete> spec, final Pageable pageable) {
        log.debug(
                "Fetching athletes with filters and pagination: page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        final Page<Athlete> athletePage = repository.findAll(spec, pageable);
        log.info(
                "Retrieved {} athletes matching filters (page {} of {})",
                athletePage.getNumberOfElements(),
                pageable.getPageNumber() + 1,
                athletePage.getTotalPages());
        return athletePage;
    }

    @Override
    public Athlete findById(final Long id) {
        log.debug("Fetching athlete with id: {}", id);
        return repository
                .findById(id)
                .map(athlete -> {
                    log.info("Found athlete: id={}, name={} {}", id, athlete.getFirstName(), athlete.getLastName());
                    return athlete;
                })
                .orElseThrow(() -> {
                    log.warn("Athlete not found with id: {}", id);
                    return new AthleteNotFoundException(id);
                });
    }

    @Override
    @Transactional
    public Athlete save(final Athlete athlete) {
        final boolean isNewAthlete = athlete.getId() == null;

        if (isNewAthlete) {
            log.debug("Creating new athlete: {} {}", athlete.getFirstName(), athlete.getLastName());

            // Check for duplicates when creating new athlete
            if (repository.existsByFirstNameAndLastNameAndBirthTimestamp(
                    athlete.getFirstName(), athlete.getLastName(), athlete.getBirthTimestamp())) {
                log.warn("Attempted to create duplicate athlete: {} {}", athlete.getFirstName(), athlete.getLastName());
                throw new DuplicateAthleteException(athlete.getFirstName(), athlete.getLastName());
            }
        } else {
            log.debug("Updating athlete with id: {}", athlete.getId());
        }

        final Athlete saved = repository.save(athlete);

        if (isNewAthlete) {
            log.info(
                    "Created new athlete: id={}, name={} {}", saved.getId(), saved.getFirstName(), saved.getLastName());
        } else {
            log.info("Updated athlete: id={}, name={} {}", saved.getId(), saved.getFirstName(), saved.getLastName());
        }

        return saved;
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        log.debug("Attempting to delete athlete with id: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Deleted athlete with id: {}", id);
        } else {
            log.warn("Attempted to delete non-existent athlete with id: {}", id);
        }
    }
}
