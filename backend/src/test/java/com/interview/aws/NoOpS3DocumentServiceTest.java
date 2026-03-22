package com.interview.aws;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.persistence.entity.SupportingDocument;
import com.interview.persistence.enums.SupportingDocumentType;

class NoOpS3DocumentServiceTest {

    private final NoOpS3DocumentService service = new NoOpS3DocumentService();

    @Test
    void generateDocumentUploads_returnsOneUploadPerDocument() {
        UUID customerId    = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        List<SupportingDocumentRequest> docs = List.of(
                SupportingDocumentRequest.builder().documentType(SupportingDocumentType.PROOF_OF_INCOME).build(),
                SupportingDocumentRequest.builder().documentType(SupportingDocumentType.GOVERNMENT_ID).build());

        List<S3DocumentService.DocumentUpload> result =
                service.generateDocumentUploads(customerId, applicationId, docs);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(S3DocumentService.DocumentUpload::documentType)
                .containsExactly(SupportingDocumentType.PROOF_OF_INCOME, SupportingDocumentType.GOVERNMENT_ID);
    }

    @Test
    void generateDocumentUploads_presignedUrlContainsObjectKey() {
        UUID customerId    = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        SupportingDocumentType type = SupportingDocumentType.PAY_STUB;
        String expectedKey = S3DocumentService.buildObjectKey(customerId, applicationId, type);

        List<S3DocumentService.DocumentUpload> result = service.generateDocumentUploads(
                customerId, applicationId,
                List.of(SupportingDocumentRequest.builder().documentType(type).build()));

        assertThat(result.get(0).presignedUrl()).contains(expectedKey);
    }

    @Test
    void generateDocumentDownloadUrls_returnsOneDownloadPerDocument() {
        List<SupportingDocument> docs = List.of(
                buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"),
                buildDoc(SupportingDocumentType.GOVERNMENT_ID,   "key-2"));

        List<S3DocumentService.DocumentDownload> result = service.generateDocumentDownloadUrls(docs);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(S3DocumentService.DocumentDownload::documentType)
                .containsExactly(SupportingDocumentType.PROOF_OF_INCOME, SupportingDocumentType.GOVERNMENT_ID);
    }

    @Test
    void generateDocumentDownloadUrls_presignedUrlContainsObjectKey() {
        String objectKey = S3DocumentService.buildObjectKey(UUID.randomUUID(), UUID.randomUUID(), SupportingDocumentType.TAX_RETURN);
        SupportingDocument doc = buildDoc(SupportingDocumentType.TAX_RETURN, objectKey);

        List<S3DocumentService.DocumentDownload> result = service.generateDocumentDownloadUrls(List.of(doc));

        assertThat(result.get(0).presignedUrl()).contains(objectKey);
    }

    @Test
    void generateDocumentDownloadUrls_emptyList_returnsEmpty() {
        assertThat(service.generateDocumentDownloadUrls(List.of())).isEmpty();
    }

    private SupportingDocument buildDoc(SupportingDocumentType type, String objectKey) {
        SupportingDocument doc = new SupportingDocument();
        doc.setDocumentType(type);
        doc.setObjectKey(objectKey);
        return doc;
    }
}
