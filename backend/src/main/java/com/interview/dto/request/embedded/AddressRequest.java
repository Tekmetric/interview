package com.interview.dto.request.embedded;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import com.interview.validation.ValidZipCode;

@Value
@Builder
@Schema(description = "Customer mailing address")
public class AddressRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(example = "100 Main St")
    String street;

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Austin")
    String city;

    @NotBlank
    @Size(min = 2, max = 2)
    @Schema(description = "2-letter US state code", example = "TX")
    String state;

    @NotBlank
    @ValidZipCode
    @Schema(example = "78701")
    String zipCode;

    public static class AddressRequestBuilder {
        public AddressRequest build() {
            Objects.requireNonNull(street,  "street is required");
            Objects.requireNonNull(city,    "city is required");
            Objects.requireNonNull(state,   "state is required");
            Objects.requireNonNull(zipCode, "zipCode is required");
            return new AddressRequest(street, city, state, zipCode);
        }
    }
}
