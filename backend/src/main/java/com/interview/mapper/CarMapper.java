package com.interview.mapper;

import com.interview.dto.car.CarCreateRequestDTO;
import com.interview.dto.car.CarDTO;
import com.interview.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = CarMappingUtils.class)
public interface CarMapper {

  @Mapping(target = "ownerId", source = "owner", qualifiedByName = "ownerToId")
  CarDTO toDto(final Car car);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "owner", ignore = true)
  Car toEntity(final CarCreateRequestDTO request);
}
