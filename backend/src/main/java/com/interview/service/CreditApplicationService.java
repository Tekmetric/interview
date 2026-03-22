package com.interview.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interview.aws.S3DocumentService;
import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.entity.SupportingDocument;
import com.interview.persistence.enums.ApplicationStatus;
import com.interview.exception.CreditApplicationNotFoundException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.DealershipException;
import com.interview.exception.InvalidApplicationStateException;
import com.interview.mapper.CreditApplicationMapper;
import com.interview.mapper.EntityMapper;
import com.interview.dto.request.CreateCreditApplicationRequest;
import com.interview.dto.request.UpdateApplicationStatusRequest;
import com.interview.dto.request.embedded.SupportingDocumentRequest;
import com.interview.dto.response.CreditApplicationResponse;
import com.interview.repository.CreditApplicationRepository;
import com.interview.repository.CustomerRepository;
import com.interview.repository.specification.CreditApplicationSpecification;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditApplicationService
        extends AbstractCrudService<CreditApplication, CreditApplicationResponse, CreateCreditApplicationRequest, UpdateApplicationStatusRequest> {

    private final CreditApplicationRepository creditApplicationRepository;
    private final CustomerRepository customerRepository;
    private final CreditApplicationMapper creditApplicationMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final S3DocumentService s3DocumentService;

    @Override
    protected JpaRepository<CreditApplication, UUID> getRepository() {
        return creditApplicationRepository;
    }

    @Override
    protected EntityMapper<CreditApplication, CreditApplicationResponse, CreateCreditApplicationRequest, UpdateApplicationStatusRequest> getMapper() {
        return creditApplicationMapper;
    }

    @Override
    protected DealershipException notFoundException(final UUID id) {
        return new CreditApplicationNotFoundException(id);
    }

    @Override
    @Transactional
    public CreditApplicationResponse create(final CreateCreditApplicationRequest request) {
        log.info("Submitting credit application for customer: {}", request.getCustomerId());
        final var customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        final CreditApplication application = creditApplicationMapper.toEntity(request);
        application.setCustomer(customer);

        final List<S3DocumentService.DocumentUpload> uploads =
                s3DocumentService.generateDocumentUploads(customer.getId(), application.getId(),
                        request.getDocuments());

        application.setDocuments(buildSupportingDocuments(application, request.getDocuments()));

        final CreditApplication saved = creditApplicationRepository.save(application);
        log.info("Credit application {} submitted for customer {} with {} document(s)",
                saved.getId(), customer.getId(), saved.getDocuments().size());

        return creditApplicationMapper.toResponse(saved)
                .toBuilder()
                .documentUploadUrls(uploads)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CreditApplicationResponse findById(final UUID id) {
        log.debug("Fetching credit application: {}", id);
        return creditApplicationRepository.findById(id)
                .map(this::withDownloadUrls)
                .orElseThrow(() -> new CreditApplicationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<CreditApplicationResponse> findAll(final ApplicationStatus status, final Pageable pageable) {
        final Specification<CreditApplication> spec = Specification
                .where(CreditApplicationSpecification.hasStatus(status));
        return creditApplicationRepository.findAll(spec, pageable)
                .map(this::withDownloadUrls);
    }

    @Transactional(readOnly = true)
    public Page<CreditApplicationResponse> findByCustomerId(final UUID customerId, final Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        return creditApplicationRepository.findByCustomerId(customerId, pageable)
                .map(this::withDownloadUrls);
    }

    @Override
    @Transactional
    public CreditApplicationResponse update(final UUID id, final UpdateApplicationStatusRequest request) {
        log.info("Transitioning application {} to status {}", id, request.getStatus());
        final CreditApplication application = creditApplicationRepository.findById(id)
                .orElseThrow(() -> new CreditApplicationNotFoundException(id));

        validateTransition(application.getStatus(), request.getStatus());
        application.setStatus(request.getStatus());

        if (request.getStatus() == ApplicationStatus.APPROVED
                || request.getStatus() == ApplicationStatus.DENIED) {
            application.setDecidedAt(Instant.now());
        }

        final CreditApplication saved = creditApplicationRepository.save(application);
        if (saved.getStatus() == ApplicationStatus.UNDER_REVIEW) {
            eventPublisher.publishEvent(new ApplicationUnderReviewEvent(saved));
        }

        return withDownloadUrls(saved);
    }

    @Override
    @Transactional
    public void delete(final UUID id) {
        log.info("Deleting credit application: {}", id);
        super.delete(id);
    }

    private CreditApplicationResponse withDownloadUrls(final CreditApplication application) {
        final List<S3DocumentService.DocumentDownload> downloads =
                s3DocumentService.generateDocumentDownloadUrls(application.getDocuments());
        return creditApplicationMapper.toResponse(application)
                .toBuilder()
                .documentDownloadUrls(downloads)
                .build();
    }

    private List<SupportingDocument> buildSupportingDocuments(
            final CreditApplication application,
            final List<SupportingDocumentRequest> requests) {
        return requests.stream()
                .map(req -> {
                    final SupportingDocument doc = new SupportingDocument();
                    doc.setApplication(application);
                    doc.setDocumentType(req.getDocumentType());
                    doc.setObjectKey(S3DocumentService.buildObjectKey(
                            application.getCustomer().getId(), application.getId(), req.getDocumentType()));
                    doc.setFileName(req.getFileName());
                    return doc;
                })
                .toList();
    }

    private void validateTransition(final ApplicationStatus current, final ApplicationStatus requested) {
        boolean valid = switch (current) {
            case SUBMITTED    -> requested == ApplicationStatus.UNDER_REVIEW;
            case UNDER_REVIEW -> requested == ApplicationStatus.APPROVED || requested == ApplicationStatus.DENIED;
            case APPROVED, DENIED -> false;
        };
        if (!valid) {
            throw new InvalidApplicationStateException(current, requested);
        }
    }
}
