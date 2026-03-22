package com.interview.exception;

public abstract sealed class AwsException extends RuntimeException
        permits S3DocumentUploadException, S3DocumentDownloadException, SqsPublishException {

    protected AwsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
