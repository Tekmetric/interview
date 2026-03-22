package com.interview.aws;

import java.util.Map;
import java.util.UUID;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.interview.exception.SqsPublishException;
import com.interview.persistence.entity.CreditApplication;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.enabled", havingValue = "true")
public class SqsPublisherImpl implements SqsPublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Override
    public void publishApplicationUnderReview(final CreditApplication application) {
        final UUID applicationId = application.getId();
        final var payload = Map.of(
                "applicationId", applicationId.toString(),
                "customerId",    application.getCustomer().getId().toString()
        );
        log.info("Publishing UNDER_REVIEW event to SQS for application id={}", applicationId);
        try {
            sqsTemplate.send(queueUrl, payload);
        } catch (final Exception e) {
            log.error("Failed to publish SQS event for application id={}", applicationId, e);
            throw new SqsPublishException(applicationId, e);
        }
    }
}
