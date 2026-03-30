package com.interview.aws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.exception.DocumentNotUploadedException;
import com.interview.exception.S3DocumentDeleteException;
import com.interview.exception.S3DocumentDownloadException;
import com.interview.exception.S3DocumentUploadException;
import com.interview.persistence.entity.SupportingDocument;
import com.interview.persistence.enums.SupportingDocumentType;

@ExtendWith(MockitoExtension.class)
class S3DocumentServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3DocumentServiceImpl s3DocumentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3DocumentService, "bucketName", "test-bucket");
    }

    @Test
    void generateDocumentUploads_returnsOneUploadPerDocument() throws MalformedURLException {
        UUID customerId    = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        List<SupportingDocumentRequest> docs = List.of(
                SupportingDocumentRequest.builder().documentType(SupportingDocumentType.PROOF_OF_INCOME).build(),
                SupportingDocumentRequest.builder().documentType(SupportingDocumentType.GOVERNMENT_ID).build());

        stubPutPresigner("https://s3.example.com/presigned");

        List<S3DocumentService.DocumentUpload> result =
                s3DocumentService.generateDocumentUploads(customerId, applicationId, docs);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(S3DocumentService.DocumentUpload::documentType)
                .containsExactly(SupportingDocumentType.PROOF_OF_INCOME, SupportingDocumentType.GOVERNMENT_ID);
    }

    @Test
    void generateDocumentUploads_presignedUrlComesFromSdk() throws MalformedURLException {
        UUID customerId    = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        String expectedUrl = "https://s3.example.com/test-bucket/some-key?sig=abc";

        stubPutPresigner(expectedUrl);

        List<S3DocumentService.DocumentUpload> result = s3DocumentService.generateDocumentUploads(
                customerId, applicationId,
                List.of(SupportingDocumentRequest.builder().documentType(SupportingDocumentType.PAY_STUB).build()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).presignedUrl()).isEqualTo(expectedUrl);
    }

    @Test
    void generateDocumentUploads_invokesPresignerOncePerDocument() throws MalformedURLException {
        stubPutPresigner("https://s3.example.com/key?sig=abc");

        s3DocumentService.generateDocumentUploads(UUID.randomUUID(), UUID.randomUUID(), List.of(
                SupportingDocumentRequest.builder().documentType(SupportingDocumentType.PROOF_OF_INCOME).build(),
                SupportingDocumentRequest.builder().documentType(SupportingDocumentType.TAX_RETURN).build()));

        verify(s3Presigner, times(2)).presignPutObject(any(Consumer.class));
    }

    @Test
    void generateDocumentDownloadUrls_returnsOneDownloadPerDocument() throws MalformedURLException {
        String objectKey1 = S3DocumentService.buildObjectKey(UUID.randomUUID(), UUID.randomUUID(), SupportingDocumentType.PROOF_OF_INCOME);
        String objectKey2 = S3DocumentService.buildObjectKey(UUID.randomUUID(), UUID.randomUUID(), SupportingDocumentType.GOVERNMENT_ID);

        List<SupportingDocument> docs = List.of(
                buildDoc(SupportingDocumentType.PROOF_OF_INCOME, objectKey1),
                buildDoc(SupportingDocumentType.GOVERNMENT_ID, objectKey2));

        stubGetPresigner("https://s3.example.com/download?sig=xyz");

        List<S3DocumentService.DocumentDownload> result =
                s3DocumentService.generateDocumentDownloadUrls(docs);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(S3DocumentService.DocumentDownload::documentType)
                .containsExactly(SupportingDocumentType.PROOF_OF_INCOME, SupportingDocumentType.GOVERNMENT_ID);
    }

    @Test
    void generateDocumentDownloadUrls_presignedUrlComesFromSdk() throws MalformedURLException {
        String expectedUrl = "https://s3.example.com/test-bucket/some-key?sig=download-abc";
        String objectKey = S3DocumentService.buildObjectKey(UUID.randomUUID(), UUID.randomUUID(), SupportingDocumentType.PAY_STUB);

        stubGetPresigner(expectedUrl);

        List<S3DocumentService.DocumentDownload> result = s3DocumentService.generateDocumentDownloadUrls(
                List.of(buildDoc(SupportingDocumentType.PAY_STUB, objectKey)));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).presignedUrl()).isEqualTo(expectedUrl);
    }

    @Test
    void generateDocumentDownloadUrls_invokesPresignerOncePerDocument() throws MalformedURLException {
        stubGetPresigner("https://s3.example.com/key?sig=get");

        s3DocumentService.generateDocumentDownloadUrls(List.of(
                buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"),
                buildDoc(SupportingDocumentType.TAX_RETURN,      "key-2")));

        verify(s3Presigner, times(2)).presignGetObject(any(Consumer.class));
    }

    @Test
    void verifyDocumentsUploaded_allObjectsExist_doesNotThrow() {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(HeadObjectResponse.builder().build());

        List<SupportingDocument> docs = List.of(
                buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"),
                buildDoc(SupportingDocumentType.GOVERNMENT_ID,   "key-2"));

        assertThatNoException().isThrownBy(() -> s3DocumentService.verifyDocumentsUploaded(docs));
        verify(s3Client, times(2)).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void verifyDocumentsUploaded_oneObjectMissing_throwsDocumentNotUploadedException() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build())
                .thenThrow(NoSuchKeyException.builder().build());

        List<SupportingDocument> docs = List.of(
                buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"),
                buildDoc(SupportingDocumentType.GOVERNMENT_ID,   "key-2"));

        assertThatThrownBy(
                () -> s3DocumentService.verifyDocumentsUploaded(docs))
                .isInstanceOf(DocumentNotUploadedException.class)
                .hasMessageContaining("GOVERNMENT_ID");
    }

    @Test
    void verifyDocumentsUploaded_allObjectsMissing_listsBothTypes() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().build());

        List<SupportingDocument> docs = List.of(
                buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"),
                buildDoc(SupportingDocumentType.TAX_RETURN,      "key-2"));

        assertThatThrownBy(
                () -> s3DocumentService.verifyDocumentsUploaded(docs))
                .isInstanceOf(DocumentNotUploadedException.class)
                .hasMessageContaining("PROOF_OF_INCOME")
                .hasMessageContaining("TAX_RETURN");
    }

    @Test
    void verifyDocumentsUploaded_sdkException_propagates() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(SdkClientException.create("network failure"));

        assertThatThrownBy(
                () -> s3DocumentService.verifyDocumentsUploaded(
                        List.of(buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"))))
                .isInstanceOf(SdkClientException.class);
    }

    @Test
    void deleteDocuments_callsDeleteObjectForEachDocument() {
        List<SupportingDocument> docs = List.of(
                buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"),
                buildDoc(SupportingDocumentType.GOVERNMENT_ID,   "key-2"));

        assertThatNoException().isThrownBy(() -> s3DocumentService.deleteDocuments(docs));
        verify(s3Client, times(2)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteDocuments_sdkException_throwsS3DocumentDeleteException() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(SdkClientException.create("simulated failure"));

        assertThatThrownBy(() -> s3DocumentService.deleteDocuments(
                List.of(buildDoc(SupportingDocumentType.PROOF_OF_INCOME, "key-1"))))
                .isInstanceOf(S3DocumentDeleteException.class)
                .hasMessageContaining("PROOF_OF_INCOME")
                .hasCauseInstanceOf(SdkClientException.class);
    }

    @Test
    void generateDocumentUploads_sdkException_throwsS3DocumentUploadException() {
        when(s3Presigner.presignPutObject(any(Consumer.class)))
                .thenThrow(SdkClientException.create("simulated credential failure"));

        assertThatThrownBy(() -> s3DocumentService.generateDocumentUploads(
                UUID.randomUUID(), UUID.randomUUID(),
                List.of(SupportingDocumentRequest.builder()
                        .documentType(SupportingDocumentType.PROOF_OF_INCOME)
                        .build())))
                .isInstanceOf(S3DocumentUploadException.class)
                .hasMessageContaining("PROOF_OF_INCOME")
                .hasCauseInstanceOf(SdkClientException.class);
    }

    @Test
    void generateDocumentDownloadUrls_sdkException_throwsS3DocumentDownloadException() {
        when(s3Presigner.presignGetObject(any(Consumer.class)))
                .thenThrow(SdkClientException.create("simulated credential failure"));

        assertThatThrownBy(() -> s3DocumentService.generateDocumentDownloadUrls(
                List.of(buildDoc(SupportingDocumentType.GOVERNMENT_ID, "some/key"))))
                .isInstanceOf(S3DocumentDownloadException.class)
                .hasMessageContaining("GOVERNMENT_ID")
                .hasCauseInstanceOf(SdkClientException.class);
    }

    private void stubPutPresigner(String url) throws MalformedURLException {
        PresignedPutObjectRequest presigned = mock(PresignedPutObjectRequest.class);
        when(presigned.url()).thenReturn(URI.create(url).toURL());
        when(s3Presigner.presignPutObject(any(Consumer.class))).thenReturn(presigned);
    }

    private void stubGetPresigner(String url) throws MalformedURLException {
        PresignedGetObjectRequest presigned = mock(PresignedGetObjectRequest.class);
        when(presigned.url()).thenReturn(URI.create(url).toURL());
        when(s3Presigner.presignGetObject(any(Consumer.class))).thenReturn(presigned);
    }

    private SupportingDocument buildDoc(SupportingDocumentType type, String objectKey) {
        SupportingDocument doc = new SupportingDocument();
        doc.setDocumentType(type);
        doc.setObjectKey(objectKey);
        return doc;
    }
}
