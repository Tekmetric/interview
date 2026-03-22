package com.interview.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Value;

import com.interview.dto.request.embedded.AddressRequest;
import com.interview.dto.request.embedded.EmploymentDetailsRequest;
import com.interview.validation.ValidPhone;

@Value
@Builder
@Schema(description = "Request payload to update an existing customer — only provided fields are changed")
public class UpdateCustomerRequest {

    @Size(max = 100)
    String firstName;

    @Size(max = 100)
    String lastName;

    @Email
    @Size(max = 255)
    String email;

    @ValidPhone
    String phone;

    @Valid
    @Schema(description = "Replacement mailing address — replaces all address fields if provided")
    AddressRequest address;

    @Valid
    @Schema(description = "Replacement employment details — replaces all employment fields if provided")
    EmploymentDetailsRequest employmentDetails;
}
