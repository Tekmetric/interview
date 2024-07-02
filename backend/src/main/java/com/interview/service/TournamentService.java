package com.interview.service;

import com.interview.dto.TournamentDto;

import java.util.List;

public interface TournamentService {
    List<TournamentDto> findAll();
}
