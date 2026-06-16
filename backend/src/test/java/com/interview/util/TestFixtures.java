package com.interview.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.interview.aws.S3DocumentService;
import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.EmploymentStatus;
import com.interview.persistence.enums.LoanPurpose;
import com.interview.persistence.enums.SupportingDocumentType;
import com.interview.dto.response.CreditApplicationResponse;
import com.interview.dto.response.CustomerResponse;

public final class TestFixtures {

    public static final UUID CUSTOMER_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static final UUID APPLICATION_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000002");

    public static final ZonedDateTime DATE_CREATED =
            ZonedDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC);

    public static final ZonedDateTime DATE_LAST_MODIFIED =
            ZonedDateTime.of(2024, 1, 16, 9, 0, 0, 0, ZoneOffset.UTC);

    private TestFixtures() {
    }

    public static CustomerResponse customerResponse() {
        return CustomerResponse.builder()
                .id(CUSTOMER_ID)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phone("+15555550100")
                .dateOfBirth(LocalDate.of(1985, 3, 15))
                .ssn("***-**-6789")
                .street("100 Main St")
                .city("Austin")
                .state("TX")
                .zipCode("78701")
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerName("Acme Corp")
                .annualIncome(new BigDecimal("95000.00"))
                .dateCreated(DATE_CREATED)
                .dateLastModified(DATE_LAST_MODIFIED)
                .build();
    }

    public static final SupportingDocumentType TEST_DOCUMENT_TYPE = SupportingDocumentType.PROOF_OF_INCOME;

    public static final String TEST_OBJECT_KEY =
            S3DocumentService.buildObjectKey(CUSTOMER_ID, APPLICATION_ID, TEST_DOCUMENT_TYPE);

    public static final S3DocumentService.DocumentUpload TEST_DOCUMENT_UPLOAD =
            new S3DocumentService.DocumentUpload(
                    TEST_DOCUMENT_TYPE,
                    "https://no-op-s3-presigned-url/" + TEST_OBJECT_KEY);

    public static CreditApplicationResponse creditApplicationResponseSubmitted() {
        return CreditApplicationResponse.builder()
                .id(APPLICATION_ID)
                .customerId(CUSTOMER_ID)
                .customerName("Jane Doe")
                .status(ApplicationStatus.SUBMITTED)
                .requestedLoanAmount(new BigDecimal("35000.00"))
                .loanPurpose(LoanPurpose.VEHICLE_PURCHASE)
                .monthlyDebt(new BigDecimal("500.00"))
                .notes("First-time buyer, stable employment for 5 years")
                .dateCreated(DATE_CREATED)
                .dateLastModified(DATE_LAST_MODIFIED)
                .documentUploadUrls(List.of(TEST_DOCUMENT_UPLOAD))
                .build();
    }

    public static CreditApplicationResponse creditApplicationResponseUnderReview() {
        return CreditApplicationResponse.builder()
                .id(APPLICATION_ID)
                .customerId(CUSTOMER_ID)
                .customerName("Jane Doe")
                .status(ApplicationStatus.UNDER_REVIEW)
                .requestedLoanAmount(new BigDecimal("35000.00"))
                .loanPurpose(LoanPurpose.VEHICLE_PURCHASE)
                .monthlyDebt(new BigDecimal("500.00"))
                .notes("First-time buyer, stable employment for 5 years")
                .dateCreated(DATE_CREATED)
                .dateLastModified(DATE_LAST_MODIFIED)
                .build();
    }
}
