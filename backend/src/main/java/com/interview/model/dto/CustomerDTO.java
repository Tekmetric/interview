package com.interview.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(
            regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Invalid phone number format"
    )
    private String phone;
    private Set<VehicleDTO> vehicles;

    public CustomerDTO(String firstName, String lastName, String phone, Set<VehicleDTO> vehicles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.vehicles = vehicles;
    }
}