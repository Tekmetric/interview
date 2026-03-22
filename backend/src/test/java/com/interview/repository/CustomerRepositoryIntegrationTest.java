package com.interview.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import com.interview.persistence.entity.Customer;
import com.interview.persistence.entity.embedded.Address;
import com.interview.persistence.entity.embedded.EmploymentDetails;
import com.interview.persistence.enums.EmploymentStatus;

@Sql("classpath:sql/customer-repository-test-data.sql")
class CustomerRepositoryIntegrationTest extends BaseIntegrationTest {

    private static final UUID JANE_ID   = UUID.fromString("c1000000-0000-7000-8000-000000000000");
    private static final UUID JOHN_ID   = UUID.fromString("c2000000-0000-7000-8000-000000000000");
    private static final UUID APP_1_ID  = UUID.fromString("a1000000-0000-7000-8000-000000000000");

    @Test
    void save_persistsCustomerAndGeneratesUuidV7Id() {
        var customer = new Customer();
        customer.setFirstName("New");
        customer.setLastName("User");
        customer.setEmail("new.user@example.com");
        customer.setPhone("+15555550199");
        customer.setDateOfBirth(LocalDate.of(1990, 6, 15));
        customer.setSsn("321-54-9870");
        customer.setAddress(Address.builder()
                .street("1 Test St").city("Austin").state("TX").zipCode("78701").build());
        customer.setEmploymentDetails(EmploymentDetails.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerName("Test Corp")
                .annualIncome(BigDecimal.valueOf(60000)).build());

        var saved = customerRepository.save(customer);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDateCreated()).isNotNull();
        assertThat(saved.getDateUpdated()).isNotNull();
    }

    @Test
    void findByEmail_returnsCustomerWhenPresent() {
        var result = customerRepository.findByEmail("jane.doe@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void findByEmail_returnsEmptyWhenAbsent() {
        assertThat(customerRepository.findByEmail("nobody@example.com")).isEmpty();
    }

    @Test
    void findAll_paginated_returnsCorrectPage() {
        Page<Customer> page = customerRepository.findAll(PageRequest.of(0, 2));

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(3);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void save_duplicateEmail_throwsDataIntegrityViolation() {
        var duplicate = new Customer();
        duplicate.setFirstName("Other");
        duplicate.setLastName("Person");
        duplicate.setEmail("jane.doe@test.com");
        duplicate.setPhone("+15555550198");
        duplicate.setDateOfBirth(LocalDate.of(1990, 1, 1));
        duplicate.setSsn("111-22-3344");
        duplicate.setAddress(Address.builder()
                .street("2 Dup St").city("Austin").state("TX").zipCode("78701").build());
        duplicate.setEmploymentDetails(EmploymentDetails.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerName("Dup Corp")
                .annualIncome(BigDecimal.valueOf(50000)).build());

        assertThatThrownBy(() -> customerRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void delete_removesCustomer() {
        var john = customerRepository.findById(JOHN_ID).orElseThrow();

        customerRepository.delete(john);

        assertThat(customerRepository.findById(JOHN_ID)).isEmpty();
    }

    @Test
    void delete_customerWithApplications_cascadesDeleteToApplications() {
        assertThat(customerRepository.findById(JANE_ID)).isPresent();
        assertThat(applicationRepository.findById(APP_1_ID)).isPresent();

        customerRepository.delete(customerRepository.findById(JANE_ID).orElseThrow());

        assertThat(customerRepository.findById(JANE_ID)).isEmpty();
        assertThat(applicationRepository.findById(APP_1_ID)).isEmpty();
    }
}
