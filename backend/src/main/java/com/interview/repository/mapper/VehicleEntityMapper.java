package com.interview.repository.mapper;

import com.interview.domain.Vehicle;
import com.interview.repository.entity.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    VehicleEntity toEntity(Vehicle vehicle);

    Vehicle toDomain(VehicleEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    void updateEntity(Vehicle vehicle, @MappingTarget VehicleEntity entity);
}
