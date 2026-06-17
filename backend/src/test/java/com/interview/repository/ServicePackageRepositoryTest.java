package com.interview.repository;

import com.interview.config.TestJpaConfig;
import com.interview.entity.Customer;
import com.interview.entity.CustomerProfile;
import com.interview.entity.ServicePackage;
import com.interview.enums.ContactMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@Transactional
@Rollback
@DisplayName("ServicePackageRepository Integration Tests")
class ServicePackageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private ServicePackage testServicePackage;
    private Customer testCustomer;
    private CustomerProfile testProfile;
    private static int packageCounter = 0;

    @BeforeEach
    void setUp() {
        packageCounter++;

        // Create test customer with profile
        testCustomer = new Customer();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe" + packageCounter + "@example.com");
        testCustomer.setPhone("+1-555-0101");

        testProfile = new CustomerProfile();
        testProfile.setAddress("123 Main St");
        testProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testProfile.setPreferredContactMethod(ContactMethod.EMAIL);
        testProfile.setCustomer(testCustomer);
        testCustomer.setCustomerProfile(testProfile);

        testCustomer = customerRepository.save(testCustomer);

        // Create test service package
        testServicePackage = new ServicePackage();
        testServicePackage.setName("Premium Package " + packageCounter);
        testServicePackage.setDescription("Full service package");
        testServicePackage.setMonthlyPrice(new BigDecimal("49.99"));
        testServicePackage.setActive(true);
        testServicePackage.setSubscribers(new HashSet<>());
    }

    @Nested
    @DisplayName("Basic Repository Operations")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should save service package")
        void shouldSaveServicePackage() {
            ServicePackage savedPackage = servicePackageRepository.save(testServicePackage);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedPackage.getId()).isNotNull();
            ServicePackage found = entityManager.find(ServicePackage.class, savedPackage.getId());
            assertThat(found).isNotNull();
            assertThat(found.getName()).isEqualTo(testServicePackage.getName());
        }

        @Test
        @DisplayName("Should enforce name uniqueness")
        void shouldEnforceNameUniqueness() {
            servicePackageRepository.save(testServicePackage);
            entityManager.flush();

            ServicePackage duplicate = new ServicePackage();
            duplicate.setName(testServicePackage.getName());
            duplicate.setDescription("Duplicate");
            duplicate.setMonthlyPrice(new BigDecimal("29.99"));
            duplicate.setActive(true);

            try {
                servicePackageRepository.save(duplicate);
                entityManager.flush();
                assertThat(false).as("Should have thrown constraint violation").isTrue();
            } catch (Exception e) {
                String message = e.getMessage().toLowerCase();
                assertThat(message).containsAnyOf("unique", "duplicate", "constraint", "violation");
            }
        }

        @Test
        @DisplayName("Should delete service package by ID")
        void shouldDeleteServicePackage() {
            ServicePackage savedPackage = servicePackageRepository.save(testServicePackage);
            entityManager.flush();
            Long id = savedPackage.getId();

            servicePackageRepository.deleteById(id);
            entityManager.flush();
            entityManager.clear();

            assertThat(servicePackageRepository.findById(id)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Custom Query Tests")
    class CustomQueryTests {

        @Test
        @DisplayName("Should check if service package exists by name")
        void shouldCheckExistsByName() {
            servicePackageRepository.save(testServicePackage);
            entityManager.flush();

            assertThat(servicePackageRepository.existsByName(testServicePackage.getName())).isTrue();
            assertThat(servicePackageRepository.existsByName("NonExistent")).isFalse();
        }

        @Test
        @DisplayName("Should find service package by ID with subscribers")
        void shouldFindByIdWithSubscribers() {
            entityManager.persist(testServicePackage);
            testCustomer.addServicePackage(testServicePackage);
            customerRepository.save(testCustomer);
            entityManager.flush();
            entityManager.clear();

            Optional<ServicePackage> result = servicePackageRepository.findByIdWithSubscribers(testServicePackage.getId());

            assertThat(result).isPresent();
            ServicePackage found = result.get();
            assertThat(found.getSubscribers()).hasSize(1);
            Customer subscriber = found.getSubscribers().iterator().next();
            assertThat(subscriber.getEmail()).isEqualTo(testCustomer.getEmail());
            assertThat(subscriber.getCustomerProfile()).isNotNull();
        }

        @Test
        @DisplayName("Should return empty when service package not found by ID")
        void shouldReturnEmptyWhenNotFound() {
            Optional<ServicePackage> result = servicePackageRepository.findByIdWithSubscribers(999L);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should find all active service packages with subscribers")
        void shouldFindAllActiveWithSubscribers() {
            ServicePackage inactive = new ServicePackage();
            inactive.setName("Inactive " + packageCounter);
            inactive.setDescription("Inactive package");
            inactive.setMonthlyPrice(new BigDecimal("19.99"));
            inactive.setActive(false);

            servicePackageRepository.save(testServicePackage);
            servicePackageRepository.save(inactive);
            entityManager.flush();
            entityManager.clear();

            List<ServicePackage> activePackages = servicePackageRepository.findAllActiveWithSubscribers();

            assertThat(activePackages).anyMatch(p -> p.getName().equals(testServicePackage.getName()));
            assertThat(activePackages).noneMatch(p -> p.getName().equals(inactive.getName()));
        }

        @Test
        @DisplayName("Should find all active service packages with subscribers using pagination")
        void shouldFindAllActiveWithSubscribersUsingPagination() {
            ServicePackage extra = new ServicePackage();
            extra.setName("Extra " + packageCounter);
            extra.setDescription("Extra active package");
            extra.setMonthlyPrice(new BigDecimal("39.99"));
            extra.setActive(true);

            servicePackageRepository.save(testServicePackage);
            servicePackageRepository.save(extra);
            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 1);
            Page<ServicePackage> page = servicePackageRepository.findAllActiveWithSubscribers(pageable);

            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
            assertThat(page.getSize()).isEqualTo(1);
            assertThat(page.getNumber()).isZero();
        }
    }

    @Nested
    @DisplayName("Subscriber Relationship Tests")
    class SubscriberRelationshipTests {

        @Test
        @DisplayName("Should find all service packages with subscribers")
        void shouldFindAllWithSubscribers() {
            entityManager.persist(testServicePackage);
            testCustomer.addServicePackage(testServicePackage);
            customerRepository.save(testCustomer);
            entityManager.flush();
            entityManager.clear();

            List<ServicePackage> allPackages = servicePackageRepository.findAllWithSubscribers();

            List<ServicePackage> testPackages = allPackages.stream()
                .filter(p -> p.getName().equals(testServicePackage.getName()))
                .toList();

            assertThat(testPackages).hasSize(1);
            ServicePackage found = testPackages.getFirst();
            assertThat(found.getSubscribers()).hasSize(1);
            Customer subscriber = found.getSubscribers().iterator().next();
            assertThat(subscriber.getEmail()).isEqualTo(testCustomer.getEmail());
        }

        @Test
        @DisplayName("Should find all service packages with subscribers using pagination")
        void shouldFindAllWithSubscribersUsingPagination() {
            ServicePackage extra = new ServicePackage();
            extra.setName("Paginated " + packageCounter);
            extra.setDescription("Paginated package");
            extra.setMonthlyPrice(new BigDecimal("59.99"));
            extra.setActive(true);

            servicePackageRepository.save(testServicePackage);
            servicePackageRepository.save(extra);
            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 1);
            Page<ServicePackage> page = servicePackageRepository.findAllWithSubscribers(pageable);

            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
            assertThat(page.getSize()).isEqualTo(1);
            assertThat(page.getNumber()).isZero();
        }
    }
}
