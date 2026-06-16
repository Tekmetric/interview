package com.interview.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.interview.persistence.entity.embedded.Address;
import com.interview.persistence.entity.embedded.EmploymentDetails;
import com.interview.persistence.enums.EmploymentStatus;

class CustomerTest {

    @Test
    void id_isAssignedAtConstruction() {
        Customer customer = buildCustomer();

        assertThat(customer.getId()).isNotNull();
    }

    @Test
    void id_twoNewCustomers_haveDistinctIds() {
        Customer a = buildCustomer();
        Customer b = buildCustomer();

        assertThat(a.getId()).isNotEqualTo(b.getId());
    }

    @Test
    void equals_twoNewCustomers_areNotEqual() {
        Customer a = buildCustomer();
        Customer b = buildCustomer();

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_sameId_areEqual() {
        Customer a = buildCustomer();
        Customer b = buildCustomer();

        b.setId(a.getId());

        assertThat(a).isEqualTo(b);
    }

    @Test
    void hashCode_isStableAcrossMutations() {
        Customer customer = buildCustomer();
        int hashAtConstruction = customer.hashCode();

        customer.setFirstName("Updated");
        customer.setEmail("updated@example.com");

        assertThat(customer.hashCode()).isEqualTo(hashAtConstruction);
    }

    @Test
    void hashCode_twoCustomersWithSameId_haveSameHashCode() {
        Customer a = buildCustomer();
        Customer b = buildCustomer();
        b.setId(a.getId());

        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }


    private Customer buildCustomer() {
        Customer c = new Customer();
        c.setFirstName("Jane");
        c.setLastName("Doe");
        c.setEmail("jane.doe@example.com");
        c.setPhone("+15555550100");
        c.setDateOfBirth(LocalDate.of(1985, 3, 15));
        c.setSsn("123-45-6789");
        c.setAddress(Address.builder()
                .street("100 Main St").city("Austin").state("TX").zipCode("78701").build());
        c.setEmploymentDetails(EmploymentDetails.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employerName("Acme Corp")
                .annualIncome(BigDecimal.valueOf(95000)).build());
        return c;
    }
}
