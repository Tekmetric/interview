package com.interview.aws;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.entity.Customer;
import com.interview.persistence.enums.ApplicationStatus;

class NoOpSqsPublisherTest {

    private final NoOpSqsPublisher publisher = new NoOpSqsPublisher();

    @Test
    void publishApplicationUnderReview_doesNotThrow() {
        CreditApplication application = new CreditApplication();
        application.setStatus(ApplicationStatus.UNDER_REVIEW);
        application.setCustomer(new Customer());

        assertThatCode(() -> publisher.publishApplicationUnderReview(application))
                .doesNotThrowAnyException();
    }
}
