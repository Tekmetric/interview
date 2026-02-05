package com.interview.mapper;

import com.interview.model.dto.VehicleDTO;
import com.interview.model.dto.VehicleServiceOrdersDTO;
import com.interview.model.entity.Vehicle;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    @Mapping(target = "customer", ignore = true)
    Vehicle toEntity(VehicleDTO dto);
    VehicleDTO toDTO(Vehicle vehicle);
    VehicleServiceOrdersDTO toServiceOrdersDTO(Vehicle vehicle);
}