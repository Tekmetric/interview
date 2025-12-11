package com.interview.dto.account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.interview.validation.ValidCountryCode;
import com.interview.validation.ValidCurrencyCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Account creation request DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequestDTO {

    @NotBlank(message = "{validation.account.name.required}")
    @Size(min = 1, max = 255, message = "{validation.account.name.size}")
    private String accountName;

    @NotBlank(message = "{validation.account.email.required}")
    @Email(message = "{validation.account.email.invalid}")
    @Size(min = 5, max = 100, message = "{validation.account.email.size}")
    private String email;

    @ValidCountryCode
    private String countryCode;

    @ValidCurrencyCode
    private String currencyCode;

    @Size(max = 255, message = "{validation.account.website.size}")
    private String website;

    @Size(max = 100, message = "{validation.account.country.size}")
    private String country;

    @Size(max = 255, message = "{validation.account.addressLine1.size}")
    private String addressLine1;

    @Size(max = 255, message = "{validation.account.addressLine2.size}")
    private String addressLine2;

    @Size(max = 100, message = "{validation.account.city.size}")
    private String city;

    @Size(max = 50, message = "{validation.account.state.size}")
    private String state;

    @Size(max = 20, message = "{validation.account.zipcode.size}")
    private String zipcode;
}

