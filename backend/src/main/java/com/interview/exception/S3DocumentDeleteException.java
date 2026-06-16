package com.interview.exception;

import com.interview.persistence.enums.SupportingDocumentType;

public final class S3DocumentDeleteException extends AwsException {

    public S3DocumentDeleteException(final SupportingDocumentType documentType, final Throwable cause) {
        super("Failed to delete S3 object for document type: " + documentType, cause);
    }
}
