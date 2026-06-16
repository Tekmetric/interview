package com.interview.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.LoanPurpose;

class CreditApplicationTest {

    @Test
    void onSubmit_setsSubmittedAt() {
        CreditApplication app = buildApplication();

        assertThat(app.getSubmittedAt()).isNull();
        app.onSubmit();
        assertThat(app.getSubmittedAt()).isNotNull();
    }

    @Test
    void defaultStatus_isSubmitted() {
        CreditApplication app = new CreditApplication();
        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.SUBMITTED);
    }

    @Test
    void versionField_inheritedFromBaseEntity() {
        CreditApplication app = buildApplication();
        assertThat(app.getVersion()).isNull();
    }

    private CreditApplication buildApplication() {
        CreditApplication app = new CreditApplication();
        app.setRequestedLoanAmount(BigDecimal.valueOf(35000));
        app.setLoanPurpose(LoanPurpose.VEHICLE_PURCHASE);
        app.setMonthlyDebt(BigDecimal.valueOf(500));
        return app;
    }
}
