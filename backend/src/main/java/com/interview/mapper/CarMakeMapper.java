package com.interview.mapper;

import com.interview.dto.CarMakeCreateDto;
import com.interview.dto.CarMakeDto;
import com.interview.dto.CarMakeUpdateDto;
import com.interview.model.CarMake;

public class CarMakeMapper {

    public static CarMake fromCreateDto(CarMakeCreateDto dto) {
        CarMake entity = new CarMake();
        entity.setName(dto.getName());
        entity.setCountry(dto.getCountry());
        entity.setFoundedYear(dto.getFoundedYear());
        return entity;
    }

    public static void applyUpdateDto(CarMakeUpdateDto dto, CarMake entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getCountry() != null) entity.setCountry(dto.getCountry());
        if (dto.getFoundedYear() != null) entity.setFoundedYear(dto.getFoundedYear());
    }

    public static CarMakeDto toDto(CarMake entity) {
        CarMakeDto dto = new CarMakeDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCountry(entity.getCountry());
        dto.setFoundedYear(entity.getFoundedYear());
        return dto;
    }
}