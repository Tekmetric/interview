package com.interview.repository;

import com.interview.model.Performer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformerRepository extends JpaRepository<Performer, UUID> {

    List<Performer> findByNameContainingIgnoreCase(String name);

    List<Performer> findByGenre(String genre);
}