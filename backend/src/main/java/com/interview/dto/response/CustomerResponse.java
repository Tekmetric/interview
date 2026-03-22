package com.interview.dto.response;

import java.util.Objects;

import com.interview.persistence.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@Builder
@Schema(description = "Customer resource representation")
public class CustomerResponse {

    @Schema(description = "Unique customer identifier (UUIDv7)")
    UUID id;

    @Schema(description = "Customer's first name", example = "Jane")
    String firstName;

    @Schema(description = "Customer's last name", example = "Doe")
    String lastName;

    @Schema(description = "Contact email address", example = "jane.doe@example.com")
    String email;

    @Schema(description = "E.164 formatted phone number", example = "+15555550100")
    String phone;

    @Schema(description = "Date of birth (customer must be 18+)", example = "1985-03-15")
    LocalDate dateOfBirth;

    @Schema(description = "SSN masked to ***-**-XXXX format", example = "***-**-6789")
    String ssn;

    @Schema(description = "Street address", example = "100 Main St")
    String street;

    @Schema(description = "City", example = "Austin")
    String city;

    @Schema(description = "Two-letter state code", example = "TX")
    String state;

    @Schema(description = "ZIP code", example = "78701")
    String zipCode;

    @Schema(description = "Current employment status", example = "EMPLOYED")
    EmploymentStatus employmentStatus;

    @Schema(description = "Employer name (if employed)", example = "Acme Corp")
    String employerName;

    @Schema(description = "Annual income in USD", example = "95000.00")
    BigDecimal annualIncome;

    @Schema(description = "Timestamp when this record was created")
    ZonedDateTime dateCreated;

    @Schema(description = "Timestamp of the most recent update to this record")
    ZonedDateTime dateLastModified;

    public static class CustomerResponseBuilder {
        public CustomerResponse build() {
            Objects.requireNonNull(id, "id is required");
            return new CustomerResponse(id, firstName, lastName, email, phone, dateOfBirth, ssn,
                    street, city, state, zipCode, employmentStatus, employerName, annualIncome,
                    dateCreated, dateLastModified);
        }
    }
}
