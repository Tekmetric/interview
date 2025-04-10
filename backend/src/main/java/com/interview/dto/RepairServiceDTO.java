package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for managing car repair service requests when a customer brings their car to the shop.
 * Contains essential information needed for the initial visit and service request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairServiceDTO {
    private Long id;
    
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;
    
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String customerPhone;
    
    @NotBlank(message = "Vehicle make is required")
    private String vehicleMake;
    
    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;
    
    @NotNull(message = "Vehicle year is required")
    @Positive(message = "Vehicle year must be a positive number")
    private Integer vehicleYear;
    
    @NotBlank(message = "License plate is required")
    private String licensePlate;
    
    private String serviceDescription;
    
    @NotNull(message = "Odometer reading is required")
    @Positive(message = "Odometer reading must be a positive number")
    private Integer odometerReading;
    
    @NotNull(message = "Status is required")
    private RepairServiceStatus status;
}
