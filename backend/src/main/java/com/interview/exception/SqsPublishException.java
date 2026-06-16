package com.interview.exception;

import java.util.UUID;

public final class SqsPublishException extends AwsException {

    public SqsPublishException(final UUID applicationId, final Throwable cause) {
        super("Failed to publish SQS event for application id: " + applicationId, cause);
    }
}
