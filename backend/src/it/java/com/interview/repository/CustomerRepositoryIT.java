package com.interview.repository;

import static com.interview.assertion.QueryAssert.assertThatQuery;
import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.PhoneNumber;
import com.interview.repository.entity.CustomerEntity;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/datasets/vehicle-data.sql")
class CustomerRepositoryIT {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    SessionFactory sessionFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = sessionFactory.getStatistics();
    }

    @Test
    void savePersistsCustomer() {
        final CustomerEntity entity = customerEntity("Jane Doe", "(555) 123-4567");

        statistics.clear();
        final CustomerEntity saved = customerRepository.save(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasInsertCount(1).hasNoOtherOperations();

        entityManager.clear();

        final CustomerEntity found = entityManager.find(CustomerEntity.class, saved.getId());
        assertThat(found).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void findByIdReturnsCustomer() {
        final CustomerEntity entity = customerEntity("Jane Doe", "(555) 123-4567");
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        statistics.clear();
        final Optional<CustomerEntity> result = customerRepository.findById(entity.getId());
        assertThatQuery(statistics).hasEntityLoadCount(1).hasNoOtherOperations();

        assertThat(result).get().usingRecursiveComparison().isEqualTo(entity);
    }

    @Test
    void findAllReturnsPage() {
        entityManager.persistAndFlush(customerEntity("Jane Doe", "(555) 123-4567"));
        entityManager.clear();

        statistics.clear();
        final Page<CustomerEntity> page = customerRepository.findAll(PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasEntityLoadCount(4).hasNoOtherOperations();

        assertThat(page.getContent()).hasSize(4);
    }

    @Test
    void deleteRemovesCustomer() {
        final CustomerEntity entity = customerEntity("Jane Doe", "(555) 123-4567");
        entityManager.persistAndFlush(entity);
        final UUID id = entity.getId();

        statistics.clear();
        customerRepository.delete(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasDeleteCount(1).hasNoOtherOperations();

        entityManager.clear();

        assertThat(customerRepository.findById(id)).isEmpty();
    }

    private CustomerEntity customerEntity(String name, String phoneNumber) {
        final CustomerEntity entity = new CustomerEntity();
        entity.setName(name);
        entity.setPhoneNumber(new PhoneNumber(phoneNumber));
        return entity;
    }
}
