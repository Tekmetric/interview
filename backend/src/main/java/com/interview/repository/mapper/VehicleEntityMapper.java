package com.interview.repository.mapper;

import com.interview.domain.Vehicle;
import com.interview.repository.entity.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface VehicleEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "customerId", target = "customer")
    VehicleEntity toEntity(Vehicle vehicle);

    @Mapping(source = "customer", target = "customerId")
    Vehicle toDomain(VehicleEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "customerId", target = "customer")
    void updateEntity(Vehicle vehicle, @MappingTarget VehicleEntity entity);
}
