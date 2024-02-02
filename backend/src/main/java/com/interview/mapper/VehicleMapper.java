package com.interview.mapper;

import com.interview.dto.VehicleDto;
import com.interview.persistence.CustomerEntity;
import com.interview.persistence.VehicleEntity;
import com.interview.service.model.Vehicle;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle dtoToModel(VehicleDto vehicle);

    @Mapping(target = "owner", source = "customer")
    @Mapping(target = "id", source = "vehicleModel.id")
    VehicleEntity modelToEntity(Vehicle vehicleModel, CustomerEntity customer);

    List<Vehicle> toModelList(List<VehicleDto> vehicles);

    List<VehicleEntity> toEntityList(List<Vehicle> vehicles);

}
