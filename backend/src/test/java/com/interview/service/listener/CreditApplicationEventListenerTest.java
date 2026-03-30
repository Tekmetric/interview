package com.interview.service.listener;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.interview.aws.SqsPublisher;
import com.interview.persistence.entity.CreditApplication;
import com.interview.service.ApplicationUnderReviewEvent;

@ExtendWith(MockitoExtension.class)
class CreditApplicationEventListenerTest {

    @Mock
    private SqsPublisher sqsPublisher;

    @InjectMocks
    private CreditApplicationEventListener listener;

    @Test
    void onApplicationUnderReview_delegatesToSqsPublisher() {
        CreditApplication application = new CreditApplication();

        listener.onApplicationUnderReview(new ApplicationUnderReviewEvent(application));

        verify(sqsPublisher).publishApplicationUnderReview(application);
    }
}
