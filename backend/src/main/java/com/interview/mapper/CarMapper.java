package com.interview.mapper;

import com.interview.dto.CarDto;
import com.interview.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarDto toDto(CarEntity entity);

    CarEntity toEntity(CarDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CarDto dto, @MappingTarget CarEntity entity);

}
