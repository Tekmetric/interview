package com.interview.mapper;

import com.interview.model.dto.ServiceOrderDTO;
import com.interview.model.entity.ServiceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceOrderMapper {

    ServiceOrder toEntity(ServiceOrderDTO dto);
    ServiceOrderDTO toDTO(ServiceOrder entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(ServiceOrderDTO dto, @MappingTarget ServiceOrder entity);
}