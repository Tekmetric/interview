package com.interview.mapper;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import com.interview.dto.OwnerUpdateRequestDTO;
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

  default void updateOwnerFromDto(final OwnerUpdateRequestDTO request, final Owner existingOwner) {
    if (request.getName() != null) {
      existingOwner.setName(request.getName());
    }
    if (request.getPersonalNumber() != null) {
      existingOwner.setPersonalNumber(request.getPersonalNumber());
    }
    if (request.getBirthDate() != null) {
      existingOwner.setBirthDate(request.getBirthDate());
    }
    if (request.getAddress() != null) {
      existingOwner.setAddress(request.getAddress());
    }
  }
}
