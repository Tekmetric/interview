package com.interview.model.account;

import com.interview.constants.AccountDbFieldConstants;
import com.interview.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Account entity for JPA/H2 database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accounts")
public class AccountEntity extends BaseEntity {

    @Column(name = AccountDbFieldConstants.ACCOUNT_REFERENCE_ID_COLUMN_NAME, unique = true, nullable = false, length = 100)
    private String accountId;

    @Column(name = AccountDbFieldConstants.ACCOUNT_NAME_COLUMN_NAME, nullable = false, length = 255)
    private String accountName;

    @Column(name = AccountDbFieldConstants.EMAIL_COLUMN_NAME, length = 100)
    private String email;

    @Column(name = AccountDbFieldConstants.WEBSITE_COLUMN_NAME, length = 255)
    private String website;

    @Column(name = AccountDbFieldConstants.COUNTRY_COLUMN_NAME, length = 100)
    private String country;

    @Column(name = AccountDbFieldConstants.COUNTRY_CODE_COLUMN_NAME, length = 10)
    private String countryCode;

    @Column(name = AccountDbFieldConstants.CURRENCY_COLUMN_NAME, length = 10)
    private String currency;

    @Column(name = AccountDbFieldConstants.ADDRESS_LINE1_COLUMN_NAME, length = 255)
    private String addressLine1;

    @Column(name = AccountDbFieldConstants.ADDRESS_LINE2_COLUMN_NAME, length = 255)
    private String addressLine2;

    @Column(name = AccountDbFieldConstants.ADDRESS_CITY_COLUMN_NAME, length = 100)
    private String city;

    @Column(name = AccountDbFieldConstants.ADDRESS_STATE_COLUMN_NAME, length = 50)
    private String state;

    @Column(name = AccountDbFieldConstants.ADDRESS_ZIPCODE_COLUMN_NAME, length = 20)
    private String zipcode;
}

