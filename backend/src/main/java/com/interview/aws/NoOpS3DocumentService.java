package com.interview.aws;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.exception.DocumentNotUploadedException;
import com.interview.persistence.entity.SupportingDocument;
import com.interview.persistence.enums.SupportingDocumentType;

@Slf4j
@Component
@ConditionalOnProperty(name = "aws.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpS3DocumentService implements S3DocumentService {

    private static final String PRESIGNED_URL_PREFIX = "https://no-op-s3-presigned-url/";

    private final Set<String> registeredKeys = ConcurrentHashMap.newKeySet();

    @Override
    public List<DocumentUpload> generateDocumentUploads(final UUID customerId, final UUID applicationId,
            final List<SupportingDocumentRequest> documents) {
        log.info("[NO-OP S3] Would generate {} presigned upload URL(s) for customer={} application={}",
                documents.size(), customerId, applicationId);
        return documents.stream()
                .map(doc -> {
                    final String objectKey = S3DocumentService.buildObjectKey(
                            customerId, applicationId, doc.getDocumentType());
                    registeredKeys.add(objectKey);
                    return new DocumentUpload(doc.getDocumentType(), buildDocumentUrl(objectKey));
                })
                .toList();
    }

    @Override
    public List<DocumentDownload> generateDocumentDownloadUrls(final List<SupportingDocument> documents) {
        log.info("[NO-OP S3] Would generate {} presigned download URL(s)", documents.size());
        return documents.stream()
                .map(doc -> new DocumentDownload(doc.getDocumentType(),
                        buildDocumentUrl(doc.getObjectKey())))
                .toList();
    }

    @Override
    public void verifyDocumentsUploaded(final List<SupportingDocument> documents) {
        log.info("[NO-OP S3] Verifying {} document(s) against registered upload keys", documents.size());
        final List<SupportingDocumentType> missing = documents.stream()
                .filter(doc -> !registeredKeys.contains(doc.getObjectKey()))
                .map(SupportingDocument::getDocumentType)
                .toList();

        if (!missing.isEmpty()) {
            throw new DocumentNotUploadedException(missing);
        }
    }

    @Override
    public void deleteDocuments(final List<SupportingDocument> documents) {
        log.info("[NO-OP S3] Would delete {} object(s)", documents.size());
        documents.forEach(doc -> registeredKeys.remove(doc.getObjectKey()));
    }

    private static String buildDocumentUrl(final String key) {
        return PRESIGNED_URL_PREFIX + key;
    }
}
