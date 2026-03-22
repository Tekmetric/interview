package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.interview.aws.S3DocumentService;
import com.interview.aws.SqsPublisher;
import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.entity.Customer;
import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.LoanPurpose;
import com.interview.dto.request.UpdateApplicationStatusRequest;
import com.interview.dto.response.CreditApplicationResponse;
import com.interview.exception.CreditApplicationNotFoundException;
import com.interview.mapper.CreditApplicationMapper;
import com.interview.repository.CreditApplicationRepository;

@SpringBootTest(
        classes = {CreditApplicationService.class, BaseIntegrationTest.Config.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CreditApplicationServiceIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    private CreditApplicationRepository creditApplicationRepository;
    @MockitoBean
    private CreditApplicationMapper creditApplicationMapper;
    @MockitoBean
    private SqsPublisher sqsPublisher;
    @MockitoBean
    private S3DocumentService s3DocumentService;

    @Autowired
    private CreditApplicationService creditApplicationService;

    @Test
    void findById_secondCallRegeneratesDownloadUrls() {
        UUID id = UUID.randomUUID();
        CreditApplication entity = submittedApplication();
        CreditApplicationResponse response = response(id, ApplicationStatus.SUBMITTED);

        when(creditApplicationRepository.findById(id))
                .thenReturn(Optional.of(entity))
                .thenReturn(Optional.of(entity));
        when(creditApplicationMapper.toResponse(entity)).thenReturn(response);
        when(s3DocumentService.generateDocumentDownloadUrls(any()))
                .thenReturn(List.of(download("https://s3.example.com/first-read")))
                .thenReturn(List.of(download("https://s3.example.com/second-read")));

        CreditApplicationResponse first = creditApplicationService.findById(id);
        CreditApplicationResponse second = creditApplicationService.findById(id);

        assertThat(first.getDocumentDownloadUrls())
                .containsExactly(download("https://s3.example.com/first-read"));
        assertThat(second.getDocumentDownloadUrls())
                .containsExactly(download("https://s3.example.com/second-read"));

        verify(creditApplicationRepository, times(2)).findById(id);
        verify(s3DocumentService, times(2)).generateDocumentDownloadUrls(any());
    }

    @Test
    void update_thenFindByIdRegeneratesDownloadUrls() {
        UUID id = UUID.randomUUID();
        CreditApplication entity = submittedApplication();
        CreditApplicationResponse underReviewResponse = response(id, ApplicationStatus.UNDER_REVIEW);

        when(creditApplicationRepository.findById(id))
                .thenReturn(Optional.of(entity))
                .thenReturn(Optional.of(entity));
        when(creditApplicationRepository.save(entity)).thenReturn(entity);
        when(creditApplicationMapper.toResponse(entity)).thenReturn(underReviewResponse);
        when(s3DocumentService.generateDocumentDownloadUrls(any()))
                .thenReturn(List.of(download("https://s3.example.com/update-response")))
                .thenReturn(List.of(download("https://s3.example.com/find-response")));

        CreditApplicationResponse updated = creditApplicationService.update(
                id, UpdateApplicationStatusRequest.builder().status(ApplicationStatus.UNDER_REVIEW).build());
        CreditApplicationResponse fetched = creditApplicationService.findById(id);

        assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.UNDER_REVIEW);
        assertThat(updated.getDocumentDownloadUrls())
                .containsExactly(download("https://s3.example.com/update-response"));
        assertThat(fetched.getDocumentDownloadUrls())
                .containsExactly(download("https://s3.example.com/find-response"));

        verify(creditApplicationRepository, times(2)).findById(id);
        verify(s3DocumentService, times(2)).generateDocumentDownloadUrls(any());
    }

    @Test
    void update_saveFails_doesNotPublishSqsEvent() {
        UUID id = UUID.randomUUID();
        CreditApplication entity = submittedApplication();

        when(creditApplicationRepository.findById(id)).thenReturn(Optional.of(entity));
        when(creditApplicationRepository.save(entity))
                .thenThrow(new ObjectOptimisticLockingFailureException(CreditApplication.class, id));

        assertThatThrownBy(() -> creditApplicationService.update(
                id, UpdateApplicationStatusRequest.builder().status(ApplicationStatus.UNDER_REVIEW).build()))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);

        verify(sqsPublisher, never()).publishApplicationUnderReview(any());
    }

    @Test
    void delete_subsequentFindByIdHitsRepository() {
        UUID id = UUID.randomUUID();
        CreditApplication entity = submittedApplication();
        CreditApplicationResponse response = response(id, ApplicationStatus.SUBMITTED);

        when(creditApplicationRepository.findById(id))
                .thenReturn(Optional.of(entity))
                .thenReturn(Optional.of(entity))
                .thenReturn(Optional.empty());
        when(creditApplicationMapper.toResponse(entity)).thenReturn(response);
        when(s3DocumentService.generateDocumentDownloadUrls(any())).thenReturn(List.of());

        creditApplicationService.findById(id);
        creditApplicationService.delete(id);

        assertThatThrownBy(() -> creditApplicationService.findById(id))
                .isInstanceOf(CreditApplicationNotFoundException.class);

        verify(creditApplicationRepository, times(3)).findById(id);
        verify(creditApplicationRepository).delete(entity);
    }

    private static CreditApplication submittedApplication() {
        CreditApplication app = new CreditApplication();
        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setCustomer(new Customer());
        app.setRequestedLoanAmount(BigDecimal.valueOf(30000));
        app.setLoanPurpose(LoanPurpose.VEHICLE_PURCHASE);
        app.setMonthlyDebt(BigDecimal.valueOf(400));
        return app;
    }

    private static CreditApplicationResponse response(UUID id, ApplicationStatus status) {
        return CreditApplicationResponse.builder()
                .id(id)
                .customerId(UUID.randomUUID())
                .status(status)
                .build();
    }

    private static S3DocumentService.DocumentDownload download(final String url) {
        return new S3DocumentService.DocumentDownload(null, url);
    }
}
