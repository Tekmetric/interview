package com.interview.mapper;

import com.interview.dto.RacquetDto;
import com.interview.entity.Racquet;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface RacquetMapper {
    RacquetDto buildDto(Racquet racquet);

    Racquet buildEntity(RacquetDto racquetDto);

    List<RacquetDto> buildDtoList(List<Racquet> racquets);

    List<Racquet> buildEntityList(List<RacquetDto> racquetDtos);
}
