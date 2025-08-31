package com.interview.mappers;

import com.interview.domain.Vehicle;
import com.interview.dtos.VehiclePatchDTO;
import com.interview.dtos.VehicleRequestDTO;
import com.interview.dtos.VehicleResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {
    VehicleResponseDTO toDto(Vehicle vehicle);

    Vehicle toEntity(VehicleResponseDTO dto);

    @Mapping(target = "id", ignore = true)
    @MappingAuditIgnore
    Vehicle toEntity(VehicleRequestDTO dto);

    @MappingAuditIgnore
    void updateEntity(VehicleRequestDTO dto, @MappingTarget Vehicle entity);

    @MappingAuditIgnore
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(VehiclePatchDTO dto, @MappingTarget Vehicle entity);
}
