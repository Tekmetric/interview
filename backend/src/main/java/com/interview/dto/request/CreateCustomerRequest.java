package com.interview.dto.request;

import java.time.LocalDate;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import com.interview.dto.request.embedded.AddressRequest;
import com.interview.dto.request.embedded.EmploymentDetailsRequest;
import com.interview.validation.ValidAdultAge;
import com.interview.validation.ValidPhone;
import com.interview.validation.ValidSSN;

@Value
@Builder
@Schema(description = "Request payload to create a new customer")
public class CreateCustomerRequest {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Customer's first name", example = "Jane")
    String firstName;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Customer's last name", example = "Doe")
    String lastName;

    @NotBlank
    @Email
    @Size(max = 255)
    @Schema(description = "Unique email address", example = "jane.doe@example.com")
    String email;

    @ValidPhone
    @Schema(description = "Phone in E.164 format", example = "+15555550100")
    String phone;

    @NotNull
    @ValidAdultAge
    @Schema(description = "Date of birth — applicant must be 18 or older", example = "1985-03-15")
    LocalDate dateOfBirth;

    @NotBlank
    @ValidSSN
    @Schema(description = "SSN in XXX-XX-XXXX format (masked to ***-**-XXXX in responses)", example = "123-45-6789")
    String ssn;

    @Valid
    @NotNull
    @Schema(description = "Mailing address")
    AddressRequest address;

    @Valid
    @NotNull
    @Schema(description = "Employment and income details")
    EmploymentDetailsRequest employmentDetails;

    public static class CreateCustomerRequestBuilder {
        public CreateCustomerRequest build() {
            Objects.requireNonNull(firstName,          "firstName is required");
            Objects.requireNonNull(lastName,           "lastName is required");
            Objects.requireNonNull(email,              "email is required");
            Objects.requireNonNull(dateOfBirth,        "dateOfBirth is required");
            Objects.requireNonNull(ssn,                "ssn is required");
            Objects.requireNonNull(address,            "address is required");
            Objects.requireNonNull(employmentDetails,  "employmentDetails is required");
            return new CreateCustomerRequest(firstName, lastName, email, phone, dateOfBirth, ssn, address, employmentDetails);
        }
    }
}
