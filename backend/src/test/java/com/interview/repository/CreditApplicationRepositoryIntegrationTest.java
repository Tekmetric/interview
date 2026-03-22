package com.interview.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.enums.ApplicationStatus;
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
}
