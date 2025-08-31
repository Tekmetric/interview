package com.interview.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record VehiclePatchDTO(
        @Size(max = 17)
        String vin,

        @Size(max = 100)
        String make,

        @Size(max = 100)
        String model,

        @Min(value = 1900)
        Integer manufactureYear,

        @Size(max = 20)
        String licensePlate,

        @Size(max = 200)
        String ownerName
) implements VehicleDTOBase {
}
