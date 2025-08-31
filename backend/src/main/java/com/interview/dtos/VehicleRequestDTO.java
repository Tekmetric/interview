package com.interview.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record VehicleRequestDTO(
        @NotBlank
        @Size(max = 17)
        String vin,

        @NotBlank
        @Size(max = 100)
        String make,

        @NotBlank
        @Size(max = 100)
        String model,

        @Min(value = 1900)
        Integer manufactureYear,

        @Size(max = 20)
        String licensePlate,

        @NotBlank
        @Size(max = 200)
        String ownerName
) implements VehicleDTOBase {
}
