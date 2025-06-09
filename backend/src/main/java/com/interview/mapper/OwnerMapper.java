package com.interview.mapper;

import com.interview.dto.car.CarDTO;
import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.owner.OwnerUpdateRequestDTO;
import com.interview.entity.Car;
import com.interview.entity.Owner;
import java.util.Optional;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = CarMappingUtils.class)
public interface OwnerMapper {

  @Mapping(target = "cars", source = "cars")
  OwnerDTO toDto(final Owner owner);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "cars", expression = "java(new java.util.HashSet<>())")
  Owner toEntity(final OwnerCreateRequestDTO request);

  default void updateOwnerFromDto(final OwnerUpdateRequestDTO request, final Owner existingOwner) {
    Optional.ofNullable(request.getName()).ifPresent(existingOwner::setName);
    Optional.ofNullable(request.getPersonalNumber()).ifPresent(existingOwner::setPersonalNumber);
    Optional.ofNullable(request.getBirthDate()).ifPresent(existingOwner::setBirthDate);
    Optional.ofNullable(request.getAddress()).ifPresent(existingOwner::setAddress);
  }

  @Mapping(target = "ownerId", source = "owner", qualifiedByName = "ownerToId")
  CarDTO toDto(final Car car);
}
