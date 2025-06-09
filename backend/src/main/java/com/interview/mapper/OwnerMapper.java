package com.interview.mapper;

import com.interview.dto.OwnerDTO;
import com.interview.entity.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OwnerMapper {

  OwnerDTO toDto(final Owner owner);
}
