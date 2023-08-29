package com.interview.mapper;

import com.interview.dto.TournamentTypeDto;
import com.interview.entity.TournamentType;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface TournamentTypeMapper {
    TournamentTypeDto buildDto(TournamentType tournamentType);

    TournamentType buildEntity(TournamentTypeDto tournamentTypeDto);

    List<TournamentTypeDto> buildDtoList(List<TournamentType> tournamentTypes);

    List<TournamentType> buildEntityList(List<TournamentTypeDto> tournamentTypeDtos);
}
