package com.interview.domain.service.common.mapper;

import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.domain.model.common.AbstractAuditingEntity;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

public interface Mapper<ENTITY extends AbstractAuditingEntity<?>, DTO extends AbstractAuditingDto<?>> {
    DTO convertToDTO(ENTITY entity);

    ENTITY convertToEntity(DTO dto);

    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "createdDate")
    @Mapping(ignore = true, target = "lastModifiedBy")
    @Mapping(ignore = true, target = "lastModifiedDate")
    @Mapping(ignore = true, target = "deleted")
    @Mapping(ignore = true, target = "id")
    ENTITY updateEntityFromDTO(DTO dto, @MappingTarget ENTITY entity);
}
