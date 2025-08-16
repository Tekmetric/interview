package com.interview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCustomerRequest {
    public String firstName;
    public String lastName;
    public String email;
    @NotNull(message = "Version is required")
    public Integer version;
}
