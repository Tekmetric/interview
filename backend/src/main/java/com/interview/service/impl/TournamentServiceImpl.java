package com.interview.service.impl;

import com.interview.dto.TournamentDto;
import com.interview.mapper.TournamentMapper;
import com.interview.repository.TournamentRepository;
import com.interview.service.TournamentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    public TournamentServiceImpl(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
    }

    @Override
    public List<TournamentDto> findAll() {
        return tournamentMapper.buildDtoList(tournamentRepository.findAll());
    }
}
