package com.interview.dto.account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.interview.validation.ValidCountryCode;
import com.interview.validation.ValidCurrencyCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Account list request DTO with filtering and pagination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountListRequestDTO {

    @Size(min = 1, max = 255, message = "{validation.account.name.size}")
    private String accountName;

    @Size(min = 1, max = 100, message = "{validation.account.name.size}")
    private String accountId;

    @ValidCurrencyCode
    private String currency;

    @ValidCountryCode
    private String countryCode;

    @Size(max = 100, message = "{validation.account.city.size}")
    private String city;

    @Size(max = 50, message = "{validation.account.state.size}")
    private String state;

    @Size(max = 20, message = "{validation.account.zipcode.size}")
    private String zipcode;

    private String status;

    @Min(value = 1, message = "{validation.account.pageNumber.min}")
    @lombok.Builder.Default
    private Integer pageNumber = 1;

    @Min(value = 1, message = "{validation.account.pageSize.min}")
    @lombok.Builder.Default
    private Integer pageSize = 25;
}

