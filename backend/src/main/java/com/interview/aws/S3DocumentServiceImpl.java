package com.interview.aws;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.exception.S3DocumentDownloadException;
import com.interview.exception.S3DocumentUploadException;
import com.interview.persistence.entity.SupportingDocument;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.enabled", havingValue = "true")
public class S3DocumentServiceImpl implements S3DocumentService {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final Duration UPLOAD_PRESIGN_DURATION   = Duration.ofMinutes(15);
    private static final Duration DOWNLOAD_PRESIGN_DURATION = Duration.ofMinutes(60);

    @Override
    public List<DocumentUpload> generateDocumentUploads(final UUID customerId, final UUID applicationId,
            final List<SupportingDocumentRequest> documents) {
        return documents.stream()
                .map(doc -> presignPut(customerId, applicationId, doc))
                .toList();
    }

    @Override
    public List<DocumentDownload> generateDocumentDownloadUrls(final List<SupportingDocument> documents) {
        return documents.stream()
                .map(this::presignGet)
                .toList();
    }

    private DocumentUpload presignPut(final UUID customerId, final UUID applicationId,
            final SupportingDocumentRequest doc) {
        final String objectKey = S3DocumentService.buildObjectKey(customerId, applicationId, doc.getDocumentType());
        try {
            final PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            final PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(r -> r
                    .signatureDuration(UPLOAD_PRESIGN_DURATION)
                    .putObjectRequest(putRequest));

            log.info("Generated presigned S3 upload URL for customer={} application={} type={} key={}",
                    customerId, applicationId, doc.getDocumentType(), objectKey);
            return new DocumentUpload(doc.getDocumentType(), presigned.url().toString());

        } catch (final SdkException e) {
            log.error("Failed to generate presigned upload URL for type={} key={}", doc.getDocumentType(), objectKey, e);
            throw new S3DocumentUploadException(doc.getDocumentType(), e);
        }
    }

    private DocumentDownload presignGet(final SupportingDocument doc) {
        try {
            final GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(doc.getObjectKey())
                    .build();

            final PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(r -> r
                    .signatureDuration(DOWNLOAD_PRESIGN_DURATION)
                    .getObjectRequest(getRequest));

            log.info("Generated presigned S3 download URL for type={} key={}",
                    doc.getDocumentType(), doc.getObjectKey());
            return new DocumentDownload(doc.getDocumentType(), presigned.url().toString());

        } catch (final SdkException e) {
            log.error("Failed to generate presigned download URL for type={} key={}",
                    doc.getDocumentType(), doc.getObjectKey(), e);
            throw new S3DocumentDownloadException(doc.getDocumentType(), e);
        }
    }
}
