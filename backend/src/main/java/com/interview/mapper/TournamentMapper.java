package com.interview.mapper;

import com.interview.dto.TournamentDto;
import com.interview.entity.Tournament;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring",
    uses = {SurfaceMapper.class},
    builder = @Builder(disableBuilder = true))
public interface TournamentMapper {
    @Mappings(value = {
        @Mapping(target = "date", dateFormat = "dd-MM-yyyy")
    })
    TournamentDto buildDto(Tournament tournament);

    @Mappings(value = {
        @Mapping(target = "date", dateFormat = "dd-MM-yyyy")
    })
    Tournament buildEntity(TournamentDto tournamentDto);

    List<TournamentDto> buildDtoList(List<Tournament> tournaments);

    List<Tournament> buildEntityList(List<TournamentDto> tournamentDtos);
}
