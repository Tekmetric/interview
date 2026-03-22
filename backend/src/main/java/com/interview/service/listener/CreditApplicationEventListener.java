package com.interview.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.interview.aws.SqsPublisher;
import com.interview.service.ApplicationUnderReviewEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditApplicationEventListener {

    private final SqsPublisher sqsPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationUnderReview(final ApplicationUnderReviewEvent event) {
        sqsPublisher.publishApplicationUnderReview(event.application());
    }
}
