package com.interview.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.enums.ApplicationStatus;
import com.interview.persistence.enums.SupportingDocumentType;
import com.interview.persistence.entity.Customer;
import com.interview.repository.specification.CreditApplicationSpecification;

@Sql("classpath:sql/credit-application-repository-test-data.sql")
class CreditApplicationRepositoryIntegrationTest extends BaseIntegrationTest {

    private static final UUID CUSTOMER_1_ID = UUID.fromString("c1000000-0000-7000-8000-000000000000");

    @Test
    void save_persistsApplicationWithCorrectDefaults() {
        Customer customer = customerRepository.getReferenceById(CUSTOMER_1_ID);

        CreditApplication app = new CreditApplication();
        app.setCustomer(customer);
        app.setRequestedLoanAmount(BigDecimal.valueOf(15000));
        app.setLoanPurpose(com.interview.persistence.enums.LoanPurpose.VEHICLE_PURCHASE);
        app.setMonthlyDebt(BigDecimal.valueOf(200));

        CreditApplication saved = applicationRepository.save(app);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(ApplicationStatus.SUBMITTED);
        assertThat(saved.getSubmittedAt()).isNotNull();
    }

    @Test
    void findByCustomerId_returnsOnlyThatCustomersApplications() {
        Page<CreditApplication> results = applicationRepository.findByCustomerId(CUSTOMER_1_ID, PageRequest.of(0, 10));

        assertThat(results.getTotalElements()).isEqualTo(2);
        results.forEach(a -> assertThat(a.getCustomer().getId()).isEqualTo(CUSTOMER_1_ID));
    }

    @Test
    void specification_filterByStatus_returnsOnlyMatchingApplications() {
        Specification<CreditApplication> spec = Specification
                .where(CreditApplicationSpecification.hasStatus(ApplicationStatus.SUBMITTED));

        Page<CreditApplication> page = applicationRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).allMatch(a -> a.getStatus() == ApplicationStatus.SUBMITTED);
    }

    @Test
    void specification_nullStatus_returnsAllApplications() {
        Specification<CreditApplication> spec = Specification.where(CreditApplicationSpecification.hasStatus(null));

        Page<CreditApplication> page = applicationRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void findByCustomerIdWithDocuments_returnsAllApplicationsWithDocumentsLoaded() {
        List<CreditApplication> results = applicationRepository.findByCustomerIdWithDocuments(CUSTOMER_1_ID);

        assertThat(results).hasSize(2);
        results.forEach(app -> assertThat(app.getCustomer().getId()).isEqualTo(CUSTOMER_1_ID));

        CreditApplication a1 = results.stream()
                .filter(a -> a.getId().equals(UUID.fromString("a1000000-0000-7000-8000-000000000000")))
                .findFirst().orElseThrow();
        assertThat(a1.getDocuments()).hasSize(2);
        assertThat(a1.getDocuments()).extracting(d -> d.getDocumentType())
                .containsExactlyInAnyOrder(SupportingDocumentType.PROOF_OF_INCOME, SupportingDocumentType.GOVERNMENT_ID);

        CreditApplication a2 = results.stream()
                .filter(a -> a.getId().equals(UUID.fromString("a2000000-0000-7000-8000-000000000000")))
                .findFirst().orElseThrow();
        assertThat(a2.getDocuments()).hasSize(1);
        assertThat(a2.getDocuments().get(0).getDocumentType()).isEqualTo(SupportingDocumentType.TAX_RETURN);
    }

    @Test
    void findByCustomerIdWithDocuments_noDuplicateApplicationsFromJoin() {
        List<CreditApplication> results = applicationRepository.findByCustomerIdWithDocuments(CUSTOMER_1_ID);

        long distinctIds = results.stream().map(CreditApplication::getId).distinct().count();
        assertThat(distinctIds).isEqualTo(results.size());
    }

    @Test
    void findByCustomerIdWithDocuments_unknownCustomer_returnsEmptyList() {
        List<CreditApplication> results = applicationRepository.findByCustomerIdWithDocuments(UUID.randomUUID());

        assertThat(results).isEmpty();
    }
}
