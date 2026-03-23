package com.interview.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSearchCriteria {
    @Pattern(regexp = "^[A-Z0-9]{17}$", message = "vin must be 17 uppercase letters or digits")
    private String vin;

    @Pattern(regexp = "^[A-Z0-9 -]{1,8}$", message = "licensePlate must be 1 to 8 uppercase letters, digits, or separators")
    private String licensePlate;

    private String make;

    private String model;

    @Min(value = 1900, message = "year must be at least 1900")
    private Integer year;
}
