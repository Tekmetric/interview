package com.interview.aws;

import java.util.List;
import java.util.UUID;

import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.persistence.entity.SupportingDocument;
import com.interview.persistence.enums.SupportingDocumentType;

public interface S3DocumentService {

    List<DocumentUpload> generateDocumentUploads(final UUID customerId, final UUID applicationId,
            final List<SupportingDocumentRequest> documents);

    List<DocumentDownload> generateDocumentDownloadUrls(final List<SupportingDocument> documents);

    void verifyDocumentsUploaded(final List<SupportingDocument> documents);

    void deleteDocuments(final List<SupportingDocument> documents);

    static String buildObjectKey(final UUID customerId, final UUID applicationId,
            final SupportingDocumentType documentType) {
        return "customers/" + customerId
                + "/applications/" + applicationId
                + "/documents/" + documentType.name().toLowerCase();
    }

    record DocumentUpload(SupportingDocumentType documentType, String presignedUrl) {}

    record DocumentDownload(SupportingDocumentType documentType, String presignedUrl) {}
}
