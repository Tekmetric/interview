package com.interview.mapper;

import com.interview.dto.PlayerDto;
import com.interview.entity.Player;
import com.interview.mapper.resolver.PlayerMapperResolver;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring",
    uses = {RacquetMapper.class,
        ScoreMapper.class,
        StatsMapper.class,
        TournamentMapper.class,
        PlayerMapperResolver.class
    },
    builder = @Builder(disableBuilder = true))
public interface PlayerMapper {
    @Mappings(value = {
        @Mapping(target = "birthdate", dateFormat = "dd-MM-yyyy"),
        @Mapping(target = "turnedPro", dateFormat = "dd-MM-yyyy"),
    })
    PlayerDto buildDto(Player player);

    @Mappings(value = {
        @Mapping(target = "birthdate", dateFormat = "dd-MM-yyyy"),
        @Mapping(target = "turnedPro", dateFormat = "dd-MM-yyyy"),
    })
    Player buildEntity(PlayerDto playerDto);

    List<PlayerDto> buildDtoList(List<Player> players);

    List<Player> buildEntityList(List<PlayerDto> playerDtos);
}
