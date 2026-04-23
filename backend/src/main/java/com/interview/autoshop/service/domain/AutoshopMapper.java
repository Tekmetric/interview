package com.interview.autoshop.service.domain;

import com.interview.autoshop.repository.AutoshopEntity;
import com.interview.autoshop.controller.dto.AutoshopResponse;
import com.interview.autoshop.controller.dto.CreateAutoshopRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AutoshopMapper {

    Autoshop toDomain(AutoshopEntity dao);

    AutoshopEntity toDao(Autoshop domain);

    Autoshop fromCreate(CreateAutoshopRequest dto);

    AutoshopResponse toResponse(Autoshop domain);
}
