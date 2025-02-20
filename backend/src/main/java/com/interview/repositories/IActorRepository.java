package com.interview.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.models.Actor;

@Repository
public interface IActorRepository extends JpaRepository<Actor, Long> {
}
