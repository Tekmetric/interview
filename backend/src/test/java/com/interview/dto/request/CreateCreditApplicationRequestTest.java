package com.interview.dto.request;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.persistence.enums.LoanPurpose;
import com.interview.persistence.enums.SupportingDocumentType;

class CreateCreditApplicationRequestTest {

    @Test
    void build_missingCustomerId_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().customerId(null).build())
                .withMessageContaining("customerId");
    }

    @Test
    void build_missingRequestedLoanAmount_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().requestedLoanAmount(null).build())
                .withMessageContaining("requestedLoanAmount");
    }

    @Test
    void build_missingLoanPurpose_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().loanPurpose(null).build())
                .withMessageContaining("loanPurpose");
    }

    @Test
    void build_missingMonthlyDebt_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().monthlyDebt(null).build())
                .withMessageContaining("monthlyDebt");
    }

    @Test
    void build_missingDocuments_throwsNullPointerException() {
        assertThatNullPointerException()
                .isThrownBy(() -> validBuilder().documents(null).build())
                .withMessageContaining("documents");
    }

    @Test
    void build_allRequiredFieldsPresent_succeeds() {
        assertThatCode(() -> validBuilder().build()).doesNotThrowAnyException();
    }

    private CreateCreditApplicationRequest.CreateCreditApplicationRequestBuilder validBuilder() {
        return CreateCreditApplicationRequest.builder()
                .customerId(UUID.randomUUID())
                .requestedLoanAmount(BigDecimal.valueOf(30000))
                .loanPurpose(LoanPurpose.VEHICLE_PURCHASE)
                .monthlyDebt(BigDecimal.valueOf(400))
                .documents(List.of(SupportingDocumentRequest.builder()
                        .documentType(SupportingDocumentType.PROOF_OF_INCOME)
                        .build()));
    }
}
