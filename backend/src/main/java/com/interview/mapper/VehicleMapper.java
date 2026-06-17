package com.interview.mapper;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between Vehicle entities and DTOs.
 *
 * <p>Handles conversions between Vehicle entities and
 * VehicleRequest/VehicleResponse DTOs with automatic code generation.
 */
@Mapper(componentModel = "spring")
public interface VehicleMapper {

    /**
     * Convert VehicleRequest to Vehicle entity.
     * Customer will be set separately in service layer.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    /**
     * Convert Vehicle entity to VehicleResponse.
     * Includes customer information for convenience.
     */
    @Named("toResponseWithCustomer")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(getCustomerFullName(vehicle))")
    @Mapping(target = "customerEmail", source = "customer.email")
    VehicleResponse toResponse(Vehicle vehicle);

    /**
     * Convert Vehicle entity to VehicleResponse (for create operations).
     * Excludes customer details to avoid lazy loading.
     */
    @Named("toCreateResponse")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "customerEmail", ignore = true)
    VehicleResponse toCreateResponse(Vehicle vehicle);

    /**
     * Convert list of Vehicle entities to list of VehicleResponse.
     */
    List<VehicleResponse> toResponseList(List<Vehicle> vehicles);

    /**
     * Update existing Vehicle entity with VehicleRequest.
     * Customer updates will be handled separately in service layer.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget Vehicle existingVehicle, VehicleRequest request);

    /**
     * Helper method to create customer full name.
     */
    default String getCustomerFullName(Vehicle vehicle) {
        if (vehicle.getCustomer() == null) {
            return null;
        }
        return vehicle.getCustomer().getFirstName() + " " + vehicle.getCustomer().getLastName();
    }
}