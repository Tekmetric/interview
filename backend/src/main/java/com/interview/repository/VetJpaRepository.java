package com.interview.repository;

import com.interview.model.Vet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class VetJpaRepository implements VetRepository {
    private final VetJpaRepositoryBase jpaRepository;

    public VetJpaRepository(VetJpaRepositoryBase jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<Vet> findByAnimalId(final Long animalId, final Pageable pageable) {
        return jpaRepository.findByAnimalId(animalId, pageable);
    }

    @Override
    public Vet save(final Vet vet) {
        return jpaRepository.save(vet);
    }

    @Override
    public Optional<Vet> findById(final Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Vet> findAll(final Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public void deleteById(final Long id) {
        jpaRepository.deleteById(id);
    }
}