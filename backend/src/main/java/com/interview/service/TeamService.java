package com.interview.service;

import com.interview.dto.TeamDto;
import com.interview.model.Team;
import com.interview.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<TeamDto> getAll() {
        return teamRepository.findAll().stream().map(Team::toDto).toList();
    }

    public Optional<TeamDto> getById(Long id) {
        return teamRepository.findById(id).map(Team::toDto);
    }

}
