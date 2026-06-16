package com.interview.aws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Map;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.interview.exception.SqsPublishException;
import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.entity.Customer;

@ExtendWith(MockitoExtension.class)
class SqsPublisherImplTest {

    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";

    @Mock
    private SqsTemplate sqsTemplate;

    @InjectMocks
    private SqsPublisherImpl sqsPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sqsPublisher, "queueUrl", QUEUE_URL);
    }

    @Test
    void publishApplicationUnderReview_payloadContainsRequiredFields() {
        CreditApplication application = buildApplication();

        ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(Map.class);
        sqsPublisher.publishApplicationUnderReview(application);

        verify(sqsTemplate).send(eq(QUEUE_URL), captor.capture());
        Map<String, String> payload = captor.getValue();

        assertThat(payload)
                .containsEntry("applicationId", application.getId().toString())
                .containsEntry("customerId",    application.getCustomer().getId().toString())
                .hasSize(2);
    }

    @Test
    void publishApplicationUnderReview_sqsTemplateThrows_throwsSqsPublishException() {
        doThrow(new RuntimeException("SQS unavailable")).when(sqsTemplate).send(any(), any());
        CreditApplication application = buildApplication();

        assertThatThrownBy(() -> sqsPublisher.publishApplicationUnderReview(application))
                .isInstanceOf(SqsPublishException.class)
                .hasMessageContaining(application.getId().toString())
                .hasCauseInstanceOf(RuntimeException.class);
    }

    private CreditApplication buildApplication() {
        CreditApplication application = new CreditApplication();
        application.setCustomer(new Customer());
        return application;
    }
}
