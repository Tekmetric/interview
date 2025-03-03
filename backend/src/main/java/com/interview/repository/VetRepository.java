package com.interview.repository;

import com.interview.model.Vet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VetRepository {
    Page<Vet> findByAnimalId(Long animalId, Pageable pageable);
    Vet save(Vet vet);
    Optional<Vet> findById(Long id);
    Page<Vet> findAll(Pageable pageable);
    void deleteById(Long id);
}