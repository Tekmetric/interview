package com.interview.mapper;

import com.interview.dto.StatsDto;
import com.interview.entity.Stats;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface StatsMapper {
    StatsDto buildDto(Stats stats);

    Stats buildEntity(StatsDto statsDto);
}
