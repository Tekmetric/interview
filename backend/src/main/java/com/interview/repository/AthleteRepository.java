package com.interview.repository;

import com.interview.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AthleteRepository extends JpaRepository<Athlete, UUID> {
}
