package com.interview.service;

import com.interview.dto.TeamDto;
import com.interview.mapper.TeamMapper;
import com.interview.model.Team;
import com.interview.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMapper teamMapper;

    public List<TeamDto> getAll() {
        return teamRepository.findAll().stream().map(teamMapper::toTeamDto).toList();
    }

    public Optional<TeamDto> getById(Long id) {
        return teamRepository.findById(id).map(teamMapper::toTeamDto);
    }

    public TeamDto create(TeamDto teamDto) {
        Team toSave = teamMapper.toTeam(teamDto);
        Team saved = teamRepository.save(toSave);
        return teamMapper.toTeamDto(saved);
    }

    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    public boolean patchById(Long id, TeamDto partialDto) {
        Optional<Team> response = teamRepository.findById(id);
        if(response.isEmpty()) {
            return false;
        }
        Team entity = response.get();

        if(partialDto.getName() != null) {
            entity.setName(partialDto.getName());
        }

        if(partialDto.getCity() != null) {
            entity.setCity(partialDto.getCity());
        }

        if(partialDto.getNumWins() != null) {
            entity.setNumWins(partialDto.getNumWins());
        }

        if(partialDto.getNumLosses() != null) {
            entity.setNumLosses(partialDto.getNumLosses());
        }

        teamRepository.save(entity);

        return true;
    }
}
