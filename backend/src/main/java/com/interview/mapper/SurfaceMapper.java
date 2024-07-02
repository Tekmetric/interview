package com.interview.mapper;

import com.interview.dto.SurfaceDto;
import com.interview.entity.Surface;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface SurfaceMapper {
    SurfaceDto buildDto(Surface surface);

    Surface buildEntity(SurfaceDto surfaceDto);

    List<SurfaceDto> buildDtoList(List<Surface> surfaces);

    List<Surface> buildEntityList(List<SurfaceDto> surfaceDtos);
}
