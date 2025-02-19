package com.interview.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.models.Director;

@Repository
public interface IDirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> findById(long id);
}
