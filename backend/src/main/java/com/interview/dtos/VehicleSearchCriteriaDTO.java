package com.interview.dtos;

import com.interview.validation.ValidVin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
@Schema(description = "Search criteria for filtering vehicles")
public record VehicleSearchCriteriaDTO(

        @Schema(description = "Vehicle Identification Number", example = "1HGBH41JXMN109186", maxLength = 17)
        @ValidVin
        String vin,

        @Schema(description = "Vehicle manufacturer", example = "BMW", maxLength = 100)
        @Size(max = 100)
        String make,

        @Schema(description = "Vehicle model", example = "X5", maxLength = 100)
        @Size(max = 100)
        String model,

        @Schema(description = "Start year for year range filter", example = "2020", minimum = "1900", maximum = "2100")
        @Min(value = 1900)
        @Max(value = 2100)
        Integer yearFrom,

        @Schema(description = "End year for year range filter", example = "2024", minimum = "1900", maximum = "2100")
        @Min(value = 1900)
        @Max(value = 2100)
        Integer yearTo,

        @Schema(description = "Exact manufacture year", example = "2023", minimum = "1900", maximum = "2100")
        @Min(value = 1900)
        @Max(value = 2100)
        Integer manufactureYear,

        @Schema(description = "License plate number", example = "ABC123", maxLength = 20)
        @Size(max = 20)
        String licensePlate,

        @Schema(description = "Vehicle owner name (supports partial matching)", example = "John Doe", maxLength = 200)
        @Size(max = 200)
        String ownerName,

        @Schema(description = "Set of vehicle models to filter by", example = "[\"X5\", \"A4\", \"C-Class\"]")
        @Size(max = 50, message = "Cannot search for more than 50 models at once")
        Set<@Size(max = 100) String> models,

        @Schema(description = "Set of vehicle makes to filter by", example = "[\"BMW\", \"Mercedes\", \"Audi\"]")
        @Size(max = 50, message = "Cannot search for more than 50 makes at once")
        Set<@Size(max = 100) String> makes,

        @Schema(description = "Filter by license plate presence", example = "true")
        Boolean hasLicensePlate,

        @Schema(description = "Filter for luxury vehicles (premium makes)", example = "true")
        Boolean isLuxuryVehicle

) {

    @Schema(hidden = true)
    public boolean hasYearRange() {
        return yearFrom != null || yearTo != null;
    }

    @Schema(hidden = true)
    public boolean hasExactYear() {
        return manufactureYear != null;
    }

    @AssertTrue(message = "Year from cannot be greater than year to")
    @Schema(hidden = true)
    public boolean isValidYearRange() {
        if (yearFrom == null || yearTo == null) {
            return true;
        }
        return yearFrom <= yearTo;
    }
}