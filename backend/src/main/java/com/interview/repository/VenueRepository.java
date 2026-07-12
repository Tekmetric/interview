package com.interview.repository;

import com.interview.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VenueRepository extends JpaRepository<Venue, UUID> {

    List<Venue> findByCity(String city);

    List<Venue> findByState(String state);

    List<Venue> findByNameContainingIgnoreCase(String name);

    List<Venue> findByCityAndState(String city, String state);
}