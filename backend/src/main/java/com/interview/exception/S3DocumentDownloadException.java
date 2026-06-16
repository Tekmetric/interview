package com.interview.exception;

import com.interview.persistence.enums.SupportingDocumentType;

public final class S3DocumentDownloadException extends AwsException {

    public S3DocumentDownloadException(final SupportingDocumentType documentType, final Throwable cause) {
        super("Failed to generate presigned download URL for document type: " + documentType, cause);
    }
}
