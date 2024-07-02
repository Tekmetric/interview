package com.interview.mapper;

import com.interview.dto.ScoreDto;
import com.interview.entity.Score;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ScoreMapper {
    ScoreDto buildDto(Score score);

    Score buildEntity(ScoreDto scoreDto);

    List<ScoreDto> buildDtoList(List<Score> scores);

    List<Score> buildEntityList(List<ScoreDto> scoreDtos);
}
