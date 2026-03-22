package com.interview.dto.request.embedded;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.interview.persistence.enums.EmploymentStatus;

class EmploymentDetailsRequestTest {

    @Test
    void build_missingEmploymentStatus_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().employmentStatus(null).build())
                .withMessageContaining("employmentStatus");
    }

    @Test
    void build_missingAnnualIncome_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().annualIncome(null).build())
                .withMessageContaining("annualIncome");
    }

    @Test
    void build_allRequiredFieldsPresent_succeeds() {
        assertThatCode(() -> validBuilder().build()).doesNotThrowAnyException();
    }

    private EmploymentDetailsRequest.EmploymentDetailsRequestBuilder validBuilder() {
        return EmploymentDetailsRequest.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .annualIncome(BigDecimal.valueOf(95000));
    }
}
