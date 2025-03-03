package com.interview.repository;

import com.interview.model.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
class AnimalJpaRepository implements AnimalRepository {
    private final AnimalJpaRepositoryBase jpaRepository;

    public AnimalJpaRepository(final AnimalJpaRepositoryBase jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Animal save(final Animal animal) {
        return jpaRepository.save(animal);
    }

    @Override
    public Optional<Animal> findById(final Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Animal> findByFilters(final String name, final LocalDate startDate, 
            final LocalDate endDate, final Long employeeId, final Pageable pageable) {
        return jpaRepository.findAll(AnimalSpecifications.withFilters(name, startDate, endDate, employeeId), pageable);
    }

    @Override
    public void deleteById(final Long id) {
        jpaRepository.deleteById(id);
    }
}