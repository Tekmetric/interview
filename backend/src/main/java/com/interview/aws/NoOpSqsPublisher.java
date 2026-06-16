package com.interview.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.interview.persistence.entity.CreditApplication;

@Slf4j
@Component
@ConditionalOnProperty(name = "aws.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpSqsPublisher implements SqsPublisher {

    @Override
    public void publishApplicationUnderReview(final CreditApplication application) {
        log.info("[NO-OP SQS] Would publish UNDER_REVIEW event for application id={} customerId={}",
                application.getId(), application.getCustomer().getId());
    }
}
