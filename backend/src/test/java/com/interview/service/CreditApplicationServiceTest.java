package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.interview.aws.S3DocumentService;
import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.entity.Customer;
import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.LoanPurpose;
import com.interview.persistence.enums.SupportingDocumentType;
import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.dto.request.CreateCreditApplicationRequest;
import com.interview.dto.request.UpdateApplicationStatusRequest;
import com.interview.dto.response.CreditApplicationResponse;
import com.interview.exception.CreditApplicationNotFoundException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.DocumentNotUploadedException;
import com.interview.exception.InvalidApplicationStateException;
import com.interview.mapper.CreditApplicationMapper;
import com.interview.repository.CreditApplicationRepository;
import com.interview.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CreditApplicationServiceTest {

    @Mock
    private CreditApplicationRepository applicationRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CreditApplicationMapper applicationMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private S3DocumentService s3DocumentService;

    @InjectMocks
    private CreditApplicationService applicationService;

    @Test
    void create_validRequest_returnsDocumentUploadUrls() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        CreateCreditApplicationRequest request = buildCreateRequest(customerId);
        CreditApplication entity = new CreditApplication();
        CreditApplicationResponse mappedResponse = buildResponse(ApplicationStatus.SUBMITTED);

        String expectedUrl = "https://s3.example.com/presigned";
        S3DocumentService.DocumentUpload upload =
                new S3DocumentService.DocumentUpload(SupportingDocumentType.PROOF_OF_INCOME, expectedUrl);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(applicationMapper.toEntity(request)).thenReturn(entity);
        when(s3DocumentService.generateDocumentUploads(customer.getId(), entity.getId(), request.getDocuments()))
                .thenReturn(List.of(upload));
        when(applicationRepository.save(entity)).thenReturn(entity);
        when(applicationMapper.toResponse(entity)).thenReturn(mappedResponse);

        CreditApplicationResponse result = applicationService.create(request);

        assertThat(result.getDocumentUploadUrls()).isNotNull();
        verify(applicationRepository).save(entity);
        verify(s3DocumentService).generateDocumentUploads(customer.getId(), entity.getId(), request.getDocuments());
        verify(eventPublisher, never()).publishEvent(any(ApplicationUnderReviewEvent.class));
    }

    @Test
    void create_persistsSupportingDocuments() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        CreateCreditApplicationRequest request = buildCreateRequest(customerId);
        CreditApplication entity = new CreditApplication();
        entity.setCustomer(customer);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(applicationMapper.toEntity(request)).thenReturn(entity);
        when(s3DocumentService.generateDocumentUploads(any(), any(), any())).thenReturn(List.of());
        when(applicationRepository.save(entity)).thenReturn(entity);
        when(applicationMapper.toResponse(entity)).thenReturn(buildResponse(ApplicationStatus.SUBMITTED));

        applicationService.create(request);

        assertThat(entity.getDocuments()).hasSize(1);
        assertThat(entity.getDocuments().get(0).getDocumentType())
                .isEqualTo(SupportingDocumentType.PROOF_OF_INCOME);
    }

    @Test
    void create_unknownCustomer_throwsCustomerNotFoundException() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.create(buildCreateRequest(customerId)))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void update_submittedToUnderReview_publishesSqsEventAfterSave() {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.SUBMITTED);
        CreditApplicationResponse response = buildResponse(ApplicationStatus.UNDER_REVIEW);

        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));
        when(applicationRepository.save(app)).thenReturn(app);
        when(applicationMapper.toResponse(app)).thenReturn(response);
        when(s3DocumentService.generateDocumentDownloadUrls(any())).thenReturn(List.of());

        applicationService.update(id, UpdateApplicationStatusRequest.builder().status(ApplicationStatus.UNDER_REVIEW).build());

        var verificationOrder = inOrder(applicationRepository, eventPublisher);
        verificationOrder.verify(applicationRepository).save(app);
        verificationOrder.verify(eventPublisher).publishEvent(any(ApplicationUnderReviewEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(ApplicationUnderReviewEvent.class));
    }

    @Test
    void update_underReviewToApproved_setsDecidedAt() {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.UNDER_REVIEW);

        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any())).thenReturn(app);
        when(applicationMapper.toResponse(any())).thenReturn(buildResponse(ApplicationStatus.APPROVED));
        when(s3DocumentService.generateDocumentDownloadUrls(any())).thenReturn(List.of());

        applicationService.update(id, UpdateApplicationStatusRequest.builder().status(ApplicationStatus.APPROVED).build());

        assertThat(app.getDecidedAt()).isNotNull();
    }

    @Test
    void update_returnsDownloadUrls() {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.SUBMITTED);
        S3DocumentService.DocumentDownload download =
                new S3DocumentService.DocumentDownload(SupportingDocumentType.PROOF_OF_INCOME, "https://s3.example.com/get");

        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));
        when(applicationRepository.save(app)).thenReturn(app);
        when(applicationMapper.toResponse(app)).thenReturn(buildResponse(ApplicationStatus.UNDER_REVIEW));
        when(s3DocumentService.generateDocumentDownloadUrls(app.getDocuments())).thenReturn(List.of(download));

        CreditApplicationResponse result = applicationService.update(id,
                UpdateApplicationStatusRequest.builder().status(ApplicationStatus.UNDER_REVIEW).build());

        assertThat(result.getDocumentDownloadUrls()).containsExactly(download);
    }

    @ParameterizedTest
    @CsvSource({
        "SUBMITTED,APPROVED",
        "SUBMITTED,DENIED",
        "APPROVED,UNDER_REVIEW",
        "APPROVED,DENIED",
        "DENIED,APPROVED",
        "DENIED,UNDER_REVIEW"
    })
    void update_invalidTransition_throwsInvalidApplicationStateException(
            ApplicationStatus current, ApplicationStatus target) {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(current);
        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> applicationService.update(id, UpdateApplicationStatusRequest.builder().status(target).build()))
                .isInstanceOf(InvalidApplicationStateException.class);
    }

    @Test
    void findByCustomerId_existingCustomer_returnsPageOfResponses() {
        UUID customerId = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.SUBMITTED);
        Page<CreditApplication> appPage = new PageImpl<>(List.of(app));
        PageRequest pageable = PageRequest.of(0, 20);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(applicationRepository.findByCustomerId(customerId, pageable)).thenReturn(appPage);
        when(applicationMapper.toResponse(app)).thenReturn(buildResponse(ApplicationStatus.SUBMITTED));
        when(s3DocumentService.generateDocumentDownloadUrls(any())).thenReturn(List.of());

        Page<CreditApplicationResponse> result = applicationService.findByCustomerId(customerId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(applicationRepository).findByCustomerId(customerId, pageable);
    }

    @Test
    void findByCustomerId_unknownCustomer_throwsCustomerNotFoundException() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> applicationService.findByCustomerId(customerId, PageRequest.of(0, 20)))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void findById_missingId_throwsCreditApplicationNotFoundException() {
        UUID id = UUID.randomUUID();
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.findById(id))
                .isInstanceOf(CreditApplicationNotFoundException.class);
    }

    @Test
    void confirmDocumentsUploaded_allPresent_returnsResponseWithDownloadUrls() {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.SUBMITTED);
        S3DocumentService.DocumentDownload download =
                new S3DocumentService.DocumentDownload(SupportingDocumentType.PROOF_OF_INCOME, "https://s3.example.com/get");

        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));
        when(applicationMapper.toResponse(app)).thenReturn(buildResponse(ApplicationStatus.SUBMITTED));
        when(s3DocumentService.generateDocumentDownloadUrls(app.getDocuments())).thenReturn(List.of(download));

        CreditApplicationResponse result = applicationService.confirmDocumentsUploaded(id);

        assertThat(result.getDocumentDownloadUrls()).containsExactly(download);
        verify(s3DocumentService).verifyDocumentsUploaded(app.getDocuments());
    }

    @Test
    void confirmDocumentsUploaded_missingDocument_throwsDocumentNotUploadedException() {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.SUBMITTED);

        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));
        org.mockito.Mockito.doThrow(new DocumentNotUploadedException(List.of(SupportingDocumentType.PROOF_OF_INCOME)))
                .when(s3DocumentService).verifyDocumentsUploaded(app.getDocuments());

        assertThatThrownBy(() -> applicationService.confirmDocumentsUploaded(id))
                .isInstanceOf(DocumentNotUploadedException.class)
                .hasMessageContaining("PROOF_OF_INCOME");
    }

    @Test
    void confirmDocumentsUploaded_unknownId_throwsCreditApplicationNotFoundException() {
        UUID id = UUID.randomUUID();
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.confirmDocumentsUploaded(id))
                .isInstanceOf(CreditApplicationNotFoundException.class);
    }

    @Test
    void delete_callsS3DeleteBeforeDatabaseDelete() {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.SUBMITTED);

        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));

        applicationService.delete(id);

        var order = inOrder(s3DocumentService, applicationRepository);
        order.verify(s3DocumentService).deleteDocuments(app.getDocuments());
        order.verify(applicationRepository).delete(app);
    }

    @Test
    void delete_unknownId_throwsCreditApplicationNotFoundException() {
        UUID id = UUID.randomUUID();
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.delete(id))
                .isInstanceOf(CreditApplicationNotFoundException.class);

        verify(s3DocumentService, never()).deleteDocuments(any());
    }

    @Test
    void confirmDocumentsUploaded_doesNotSaveApplication() {
        UUID id = UUID.randomUUID();
        CreditApplication app = buildApplication(ApplicationStatus.SUBMITTED);

        when(applicationRepository.findById(id)).thenReturn(Optional.of(app));
        when(applicationMapper.toResponse(app)).thenReturn(buildResponse(ApplicationStatus.SUBMITTED));
        when(s3DocumentService.generateDocumentDownloadUrls(any())).thenReturn(List.of());

        applicationService.confirmDocumentsUploaded(id);

        verify(applicationRepository, never()).save(any());
    }

    private CreateCreditApplicationRequest buildCreateRequest(UUID customerId) {
        return CreateCreditApplicationRequest.builder()
                .customerId(customerId)
                .requestedLoanAmount(BigDecimal.valueOf(30000))
                .loanPurpose(LoanPurpose.VEHICLE_PURCHASE)
                .monthlyDebt(BigDecimal.valueOf(400))
                .documents(List.of(SupportingDocumentRequest.builder()
                        .documentType(SupportingDocumentType.PROOF_OF_INCOME)
                        .build()))
                .build();
    }

    private CreditApplication buildApplication(ApplicationStatus status) {
        CreditApplication app = new CreditApplication();
        app.setStatus(status);
        app.setCustomer(new Customer());
        return app;
    }

    private CreditApplicationResponse buildResponse(ApplicationStatus status) {
        return CreditApplicationResponse.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .status(status)
                .build();
    }
}
