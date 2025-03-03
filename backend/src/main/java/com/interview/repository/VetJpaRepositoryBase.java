package com.interview.repository;

import com.interview.model.Vet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface VetJpaRepositoryBase extends JpaRepository<Vet, Long> {
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.animals a WHERE a.id = :animalId")
    Page<Vet> findByAnimalId(@Param("animalId") final Long animalId, final Pageable pageable);
}