package com.interview.service;

import com.interview.config.minio.MinioProperties;
import com.interview.dto.estimation.EstimationDto;
import com.interview.dto.estimation.EstimationInfo;
import com.interview.dto.estimation.EstimationPdfInfo;
import com.interview.model.EstimationStatus;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimationService {

    private final EstimationPersistenceService estimationPersistenceService;
    private final EstimationPdfGenerator estimationPdfGenerator;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public EstimationDto submitEstimation(long repairOrderId) {
        log.info("Submitting estimation for repairOrderId: {}", repairOrderId);
        estimationPersistenceService.updateEstimationStatus(repairOrderId, EstimationStatus.IN_PROGRESS);

        Thread.ofVirtual().start(() -> {
            try {
                processEstimation(repairOrderId);
            } catch (Exception e) {
                log.error("Failed to process estimation for repairOrderId: {}", repairOrderId);
                estimationPersistenceService.markEstimationAsFailed(repairOrderId);
            }
        });
        return new EstimationDto(EstimationStatus.IN_PROGRESS);
    }

    private void processEstimation(long repairOrderId) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        EstimationInfo estimationInfo = estimationPersistenceService.getEstimationInfo(repairOrderId);
        try (InputStream pdf = estimationPdfGenerator.generateEstimatePdf(estimationInfo)) {
            String pdfName = UUID.randomUUID().toString();
            uploadPdf(pdf, pdfName);
            estimationPersistenceService.markEstimationAsCompleted(repairOrderId, pdfName);
        }
    }

    public EstimationDto getEstimation(long repairOrderId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("Getting estimation for repair orderId: {}", repairOrderId);
        EstimationPdfInfo pdfInfo = estimationPersistenceService.getEstimationPdfStatus(repairOrderId);
        if (pdfInfo.estimationStatus() == EstimationStatus.COMPLETED) {
            log.info("Getting presigned url for repair orderId:{} and pdf key: {}", repairOrderId, pdfInfo);
            String presignedUrl = getPresignedUrl(pdfInfo.pdfKey());
            return new EstimationDto(EstimationStatus.COMPLETED, presignedUrl);
        }
        return new EstimationDto(pdfInfo.estimationStatus());
    }

    private void uploadPdf(InputStream stream, String name) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        var response = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(name)
                        .stream(stream, stream.available(), -1)
                        .contentType(MediaType.APPLICATION_PDF_VALUE)
                        .build()
        );
        log.info("Successfully uploaded pdf name: {} to bucket: {}", name, response.bucket());
    }

    private String getPresignedUrl(String pdfKey) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(minioProperties.bucket())
                .object(pdfKey)
                .expiry(minioProperties.presignedUrlExpireAfterMinutes(), TimeUnit.MINUTES)
                .build());
    }
}
