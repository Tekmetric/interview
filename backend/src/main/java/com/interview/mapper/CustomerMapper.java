package com.interview.mapper;

import com.interview.model.dto.CustomerDTO;
import com.interview.model.entity.Customer;
import com.interview.model.entity.Vehicle;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "vehicleIds", source = "vehicles", qualifiedByName = "mapVehiclesToIds")
    CustomerDTO toDto(Customer customer);

    @Mapping(target = "vehicles", ignore = true)
    Customer toEntity(CustomerDTO dto);

    // Handles partial updates with MapStruct
    void updateEntityFromDto(CustomerDTO dto, @MappingTarget Customer existing);

    @Named("mapVehiclesToIds")
    default List<Long> mapVehiclesToIds(List<Vehicle> vehicles) {
        if (vehicles == null) return null;
        return vehicles.stream()
                .map(Vehicle::getId)
                .collect(Collectors.toList());
    }
}