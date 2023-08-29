package com.interview.service.impl;

import com.interview.dto.RacquetDto;
import com.interview.mapper.RacquetMapper;
import com.interview.repository.RacquetRepository;
import com.interview.service.RacquetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RacquetServiceImpl implements RacquetService {
    private final RacquetRepository racquetRepository;
    private final RacquetMapper racquetMapper;

    public RacquetServiceImpl(RacquetRepository racquetRepository, RacquetMapper racquetMapper) {
        this.racquetRepository = racquetRepository;
        this.racquetMapper = racquetMapper;
    }

    @Override
    public List<RacquetDto> findAll() {
        return racquetMapper.buildDtoList(racquetRepository.findAll());
    }
}
