package com.interview.repository;

import com.interview.model.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface AnimalRepository {
    Animal save(Animal animal);
    Optional<Animal> findById(Long id);
    Page<Animal> findByFilters(String name, LocalDate startDate, LocalDate endDate, Long employeeId, Pageable pageable);
    void deleteById(Long id);
}