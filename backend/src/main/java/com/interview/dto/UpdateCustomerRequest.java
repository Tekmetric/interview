package com.interview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// No password, password update is a separate api
// No role, role is not allowed to update by this api
// No addresses, address update is a separate api
// No id, createdAt since they are automatically generated
@Data
public class UpdateCustomerRequest {
    public String firstName;
    public String lastName;
    public String email;
    @NotNull(message = "Version is required")
    public Integer version;
}
