package com.interview.dtos;

import com.interview.validation.ValidVin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Vehicle")
public record VehicleRequestDTO(
        @NotBlank
        @ValidVin
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
