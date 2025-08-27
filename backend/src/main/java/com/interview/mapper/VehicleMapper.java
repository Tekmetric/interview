package com.interview.mapper;

import com.interview.domain.Vehicle;
import com.interview.dto.UpsertVehicleDto;
import com.interview.dto.VehicleDto;
import com.interview.dto.search.VehicleSearchCriteriaDto;
import com.interview.filter.VehicleQueryFilter;

import java.time.Year;

public class VehicleMapper {

    public static VehicleQueryFilter toQueryFilter(final VehicleSearchCriteriaDto vehicleSearchCriteria) {
        return VehicleQueryFilter.builder()
                .includingVehicleTypes(vehicleSearchCriteria.includingVehicleTypes())
                .excludingVehicleTypes(vehicleSearchCriteria.excludingVehicleTypes())
                .productionYearFrom(vehicleSearchCriteria.productionYearFrom() != null ? Year.parse(vehicleSearchCriteria.productionYearFrom()) : null)
                .productionYearTo(vehicleSearchCriteria.productionYearTo() != null ? Year.parse(vehicleSearchCriteria.productionYearTo()) : null)
                .includingVins(vehicleSearchCriteria.includingVins())
                .excludingVins(vehicleSearchCriteria.excludingVins()).build();
    }

    public static Vehicle toEntity(final UpsertVehicleDto vehicleDto) {
        return Vehicle.builder()
                .type(vehicleDto.type())
                .productionYear(Year.parse(vehicleDto.productionYear()))
                .vin(vehicleDto.vin())
                .model(vehicleDto.model())
                .make(vehicleDto.make())
                .build();
    }

    public static VehicleDto toDto(final Vehicle vehicle) {
        return VehicleDto.builder()
                .id(vehicle.getId())
                .type(vehicle.getType())
                .productionYear(vehicle.getProductionYear())
                .vin(vehicle.getVin())
                .model(vehicle.getModel())
                .make(vehicle.getMake())
                .createdDate(vehicle.getCreatedDate())
                .lastModifiedDate(vehicle.getLastModifiedDate())
                .createdBy(vehicle.getCreatedBy())
                .lastModifiedBy(vehicle.getLastModifiedBy()).build();
    }

}
