package com.interview.autoshop.service.domain;

import com.interview.autoshop.controller.dto.AutoshopResponse;
import com.interview.autoshop.controller.dto.CreateAutoshopRequest;
import com.interview.autoshop.controller.dto.UpdateAutoshopRequest;
import com.interview.autoshop.repository.AutoshopEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AutoshopMapper {

    Autoshop toDomain(AutoshopEntity entity);

    AutoshopEntity toEntity(Autoshop domain);

    Autoshop fromCreate(CreateAutoshopRequest dto);

    void applyUpdate(UpdateAutoshopRequest dto, @MappingTarget AutoshopEntity entity);

    AutoshopResponse toResponse(Autoshop domain);
}
