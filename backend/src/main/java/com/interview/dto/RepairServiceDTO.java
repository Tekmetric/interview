package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for repair service information")
public class RepairServiceDTO {
    @Schema(description = "Unique identifier for the repair service", example = "1")
    private Long id;
    
    @Schema(description = "Name of the customer", example = "John Doe", requiredMode = REQUIRED)
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;
    
    @Schema(description = "Customer phone number", example = "5551234567", requiredMode = REQUIRED)
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String customerPhone;
    
    @Schema(description = "Make of the vehicle", example = "Toyota", requiredMode = REQUIRED)
    @NotBlank(message = "Vehicle make is required")
    private String vehicleMake;
    
    @Schema(description = "Model of the vehicle", example = "Camry", requiredMode = REQUIRED)
    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;
    
    @Schema(description = "Year of the vehicle", example = "2022", requiredMode = REQUIRED)
    @NotNull(message = "Vehicle year is required")
    @Positive(message = "Vehicle year must be a positive number")
    private Integer vehicleYear;
    
    @Schema(description = "License plate of the vehicle", example = "ABC123", requiredMode = REQUIRED)
    @NotBlank(message = "License plate is required")
    private String licensePlate;
    
    @Schema(description = "Description of the service to be performed", example = "Oil change and tire rotation")
    private String serviceDescription;
    
    @Schema(description = "Current odometer reading of the vehicle", example = "45000", requiredMode = REQUIRED)
    @NotNull(message = "Odometer reading is required")
    @Positive(message = "Odometer reading must be a positive number")
    private Integer odometerReading;
    
    @Schema(description = "Current status of the repair service", requiredMode = REQUIRED)
    @NotNull(message = "Status is required")
    private RepairServiceStatus status;
}
