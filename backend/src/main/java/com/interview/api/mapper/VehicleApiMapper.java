package com.interview.api.mapper;

import com.interview.api.request.VehicleRequest;
import com.interview.api.response.VehicleResponse;
import com.interview.domain.Vehicle;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleApiMapper {

    @Mapping(target = "id", ignore = true)
    Vehicle toDomain(VehicleRequest request);

    Vehicle toDomain(UUID id, VehicleRequest request);

    VehicleResponse toResponse(Vehicle vehicle);
}
