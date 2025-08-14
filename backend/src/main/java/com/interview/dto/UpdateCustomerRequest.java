package com.interview.dto;

import lombok.Data;

@Data
public class UpdateCustomerRequest {
    public String firstName;
    public String lastName;
    public String email;
}
