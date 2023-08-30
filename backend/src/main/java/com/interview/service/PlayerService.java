package com.interview.service;

import com.interview.dto.PlayerDto;
import com.interview.exception.PlayerServiceException;

import java.util.List;

public interface PlayerService {
    List<PlayerDto> findAll(String query);
    PlayerDto findById(Long id) throws PlayerServiceException;
    PlayerDto save(PlayerDto playerDto) throws PlayerServiceException;
    void delete(Long id);
}
