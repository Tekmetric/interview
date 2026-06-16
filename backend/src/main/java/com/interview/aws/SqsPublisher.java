package com.interview.aws;

import com.interview.persistence.entity.CreditApplication;

public interface SqsPublisher {

    void publishApplicationUnderReview(final CreditApplication application);
}
