package com.interview.dto.request;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.interview.dto.request.embedded.AddressRequest;
import com.interview.dto.request.embedded.EmploymentDetailsRequest;
import com.interview.persistence.enums.EmploymentStatus;

class CreateCustomerRequestTest {

    @Test
    void build_missingFirstName_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().firstName(null).build())
                .withMessageContaining("firstName");
    }

    @Test
    void build_missingLastName_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().lastName(null).build())
                .withMessageContaining("lastName");
    }

    @Test
    void build_missingEmail_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().email(null).build())
                .withMessageContaining("email");
    }

    @Test
    void build_missingDateOfBirth_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().dateOfBirth(null).build())
                .withMessageContaining("dateOfBirth");
    }

    @Test
    void build_missingSsn_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().ssn(null).build())
                .withMessageContaining("ssn");
    }

    @Test
    void build_missingAddress_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().address(null).build())
                .withMessageContaining("address");
    }

    @Test
    void build_missingEmploymentDetails_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().employmentDetails(null).build())
                .withMessageContaining("employmentDetails");
    }

    @Test
    void build_allRequiredFieldsPresent_succeeds() {
        assertThatCode(() -> validBuilder().build()).doesNotThrowAnyException();
    }

    private CreateCustomerRequest.CreateCustomerRequestBuilder validBuilder() {
        AddressRequest address = AddressRequest.builder()
                .street("100 Main St").city("Austin").state("TX").zipCode("78701").build();
        EmploymentDetailsRequest employment = EmploymentDetailsRequest.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED).annualIncome(BigDecimal.valueOf(95000)).build();
        return CreateCustomerRequest.builder()
                .firstName("Jane").lastName("Doe").email("jane@example.com")
                .dateOfBirth(LocalDate.of(1985, 3, 15)).ssn("123-45-6789")
                .address(address).employmentDetails(employment);
    }
}
