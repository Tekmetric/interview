package com.interview.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.interview.Entities.OlympicTeam;

public interface OlympicTeamsRepository extends JpaRepository<OlympicTeam, Long> {

    OlympicTeam findById(Integer id);

    @Query(value = "SELECT * FROM olympicteams ot WHERE ot.disabled_on IS NULL", nativeQuery = true)
    List<OlympicTeam> findByDisabledOnIsNotNull();

}
