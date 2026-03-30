package com.interview.exception;

import java.util.List;

import com.interview.persistence.enums.SupportingDocumentType;

public final class DocumentNotUploadedException extends DealershipException {

    public DocumentNotUploadedException(final List<SupportingDocumentType> missingTypes) {
        super("The following documents have not been uploaded to S3: " + missingTypes);
    }
}
