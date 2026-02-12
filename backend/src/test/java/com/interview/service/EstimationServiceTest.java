package com.interview.service;

import com.interview.config.minio.MinioProperties;
import com.interview.dto.estimation.EstimationDto;
import com.interview.dto.estimation.EstimationPdfInfo;
import com.interview.model.EstimationStatus;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EstimationServiceTest {

    @Mock
    private EstimationPersistenceService estimationPersistenceService;

    @Mock
    private EstimationPdfGenerator estimationPdfGenerator;

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @InjectMocks
    private EstimationService estimationService;

    @Test
    void submitEstimation_shouldReturnInProgressAndCallPersistence() {
        long repairOrderId = 1L;

        // when
        EstimationDto result = estimationService.submitEstimation(repairOrderId);

        // then
        assertThat(result.estimationStatus()).isEqualTo(EstimationStatus.IN_PROGRESS);
    }

    @Test
    void getEstimation_shouldReturnCompletedWithPresignedUrl() throws Exception {
        long repairOrderId = 1L;
        EstimationPdfInfo pdfInfo = new EstimationPdfInfo(EstimationStatus.COMPLETED, "pdfKey");

        when(estimationPersistenceService.getEstimationPdfStatus(repairOrderId)).thenReturn(pdfInfo);
        when(minioProperties.bucket()).thenReturn("bucket");
        when(minioProperties.presignedUrlExpireAfterMinutes()).thenReturn(10);
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://presigned.url");

        // when
        EstimationDto result = estimationService.getEstimation(repairOrderId);

        // then
        assertThat(result.estimationStatus()).isEqualTo(EstimationStatus.COMPLETED);
        assertThat(result.pdfUrl()).isEqualTo("http://presigned.url");
        verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void getEstimation_shouldReturnInProgressWhenNotCompleted() throws Exception {
        long repairOrderId = 1L;
        EstimationPdfInfo pdfInfo = new EstimationPdfInfo(EstimationStatus.IN_PROGRESS, "pdfKey");

        when(estimationPersistenceService.getEstimationPdfStatus(repairOrderId)).thenReturn(pdfInfo);

        // when
        EstimationDto result = estimationService.getEstimation(repairOrderId);

        // then
        assertThat(result.estimationStatus()).isEqualTo(EstimationStatus.IN_PROGRESS);
        verifyNoInteractions(minioClient);
    }
}