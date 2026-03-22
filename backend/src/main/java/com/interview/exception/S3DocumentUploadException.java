package com.interview.exception;

import com.interview.persistence.enums.SupportingDocumentType;

public final class S3DocumentUploadException extends AwsException {

    public S3DocumentUploadException(final SupportingDocumentType documentType, final Throwable cause) {
        super("Failed to generate presigned upload URL for document type: " + documentType, cause);
    }
}
