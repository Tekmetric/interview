package com.interview.mapper;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import com.interview.entity.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OwnerMapper {

  OwnerDTO toDto(final Owner owner);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Owner toEntity(final OwnerCreateRequestDTO request);
}
