package com.interview.domain.service.common.mapper;

import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.domain.model.common.AbstractAuditingEntity;

/**
 * To be inherited by all Mapper classes
 */
public abstract class MapperImpl<ENTITY extends AbstractAuditingEntity<?>, DTO extends AbstractAuditingDto<?>> implements Mapper<ENTITY, DTO> {
    public abstract ENTITY convertToEntityImpl(DTO entitydto);

    public abstract DTO convertToEntityDTOImpl(ENTITY entity);

    @Override
    public ENTITY convertToEntity(DTO dto) {
        if (dto == null) {
            return null;
        }
        return convertToEntityImpl(dto);
    }

    @Override
    public DTO convertToDTO(ENTITY entity) {
        if (entity == null) {
            return null;
        }
        return convertToEntityDTOImpl(entity);
    }
}
