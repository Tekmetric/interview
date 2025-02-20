package com.interview.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.models.Director;

@Repository
public interface IDirectorRepository extends JpaRepository<Director, Long> {

}
