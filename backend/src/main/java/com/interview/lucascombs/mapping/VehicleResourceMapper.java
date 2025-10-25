package com.interview.lucascombs.mapping;

import com.interview.lucascombs.entity.Vehicle;
import com.interview.lucascombs.resource.VehicleResource;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleResourceMapper {

    @Mapping(source = "id", target = "vehicleId")
    @Mapping(source = "ownersName", target = "owner")
    VehicleResource toResource(Vehicle vehicle);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    Vehicle toEntity(VehicleResource vehicleResource);

    @InheritConfiguration
    Vehicle updateEntity(VehicleResource vehicleResource, @MappingTarget Vehicle vehicle);
}
