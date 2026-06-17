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
@Import(TestJpaConfig.class) // Import test JPA auditing configuration
@Transactional
@Rollback
@DisplayName("CustomerRepository Integration Tests")
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;
    private CustomerProfile testProfile;
    private ServicePackage testServicePackage;
    private static int customerCounter = 0;

    @BeforeEach
    void setUp() {
        // Increment counter to ensure unique emails across tests
        customerCounter++;

        // Create test customer
        testCustomer = new Customer();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe" + customerCounter + "@example.com"); // Unique email
        testCustomer.setPhone("+1-555-0101");
        // Don't set version - let JPA manage it
        testCustomer.setSubscribedPackages(new HashSet<>());

        // Create test profile
        testProfile = new CustomerProfile();
        testProfile.setAddress("123 Main St");
        testProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testProfile.setPreferredContactMethod(ContactMethod.EMAIL);
        testProfile.setCustomer(testCustomer);
        testCustomer.setCustomerProfile(testProfile);

        // Create test service package
        testServicePackage = new ServicePackage();
        testServicePackage.setName("Basic Maintenance " + customerCounter); // Unique name
        testServicePackage.setDescription("Essential maintenance");
        testServicePackage.setMonthlyPrice(new BigDecimal("29.99"));
        testServicePackage.setActive(true);
        testServicePackage.setSubscribers(new HashSet<>());
    }

    @Nested
    @DisplayName("Basic Repository Operations")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should save customer with profile")
        void shouldSaveCustomerWithProfile() {
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedCustomer.getId()).isNotNull();
            assertThat(savedCustomer.getCustomerProfile()).isNotNull();
            assertThat(savedCustomer.getCustomerProfile().getId()).isNotNull();

            Customer foundCustomer = entityManager.find(Customer.class, savedCustomer.getId());
            assertThat(foundCustomer).isNotNull();
            assertThat(foundCustomer.getEmail()).isEqualTo(testCustomer.getEmail()); // Use actual email
        }

        @Test
        @DisplayName("Should save customer without profile")
        void shouldSaveCustomerWithoutProfile() {
            testCustomer.setCustomerProfile(null);

            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();

            assertThat(savedCustomer.getId()).isNotNull();
            assertThat(savedCustomer.getCustomerProfile()).isNull();
        }

        @Test
        @DisplayName("Should enforce email uniqueness constraint")
        void shouldEnforceEmailUniqueness() {
            customerRepository.save(testCustomer);
            entityManager.flush();

            Customer duplicateCustomer = new Customer();
            duplicateCustomer.setFirstName("Jane");
            duplicateCustomer.setLastName("Smith");
            duplicateCustomer.setEmail(testCustomer.getEmail()); // Same email as testCustomer
            // Don't set version - let JPA manage it

            try {
                customerRepository.save(duplicateCustomer);
                entityManager.flush();
                assertThat(false).as("Should have thrown constraint violation").isTrue();
            } catch (Exception e) {
                // H2 uses "Unique index or primary key violation" message
                String message = e.getMessage().toLowerCase();
                assertThat(message).containsAnyOf("unique", "duplicate", "constraint", "violation");
            }
        }
    }

    @Nested
    @DisplayName("Custom Query Tests")
    class CustomQueryTests {

        @Test
        @DisplayName("Should check if customer exists by email")
        void shouldCheckExistsByEmail() {
            customerRepository.save(testCustomer);
            entityManager.flush();

            assertThat(customerRepository.existsByEmail("john.doe@example.com")).isTrue();
            assertThat(customerRepository.existsByEmail("nonexistent@example.com")).isFalse();
        }

        @Test
        @DisplayName("Should find customer by ID with profile")
        void shouldFindByIdWithProfile() {
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            entityManager.clear(); // Clear persistence context to test fetch join

            Optional<Customer> result = customerRepository.findByIdWithProfile(savedCustomer.getId());

            assertThat(result).isPresent();
            Customer foundCustomer = result.get();
            assertThat(foundCustomer.getEmail()).isEqualTo(testCustomer.getEmail()); // Use actual email

            // Verify profile is loaded (no lazy loading exception)
            assertThat(foundCustomer.getCustomerProfile()).isNotNull();
            assertThat(foundCustomer.getCustomerProfile().getAddress()).isEqualTo("123 Main St");
            assertThat(foundCustomer.getCustomerProfile().getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        }

        @Test
        @DisplayName("Should find customer by ID with profile when no profile exists")
        void shouldFindByIdWithProfileWhenNoProfile() {
            testCustomer.setCustomerProfile(null);
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            entityManager.clear();

            Optional<Customer> result = customerRepository.findByIdWithProfile(savedCustomer.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getCustomerProfile()).isNull();
        }

        @Test
        @DisplayName("Should return empty when customer not found by ID")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Customer> result = customerRepository.findByIdWithProfile(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should find all customers with profiles")
        void shouldFindAllWithProfiles() {
            Customer customer2 = new Customer();
            customer2.setFirstName("Jane");
            customer2.setLastName("Smith");
            customer2.setEmail("jane.smith" + customerCounter + "@example.com"); // Make unique

            customerRepository.save(testCustomer);
            customerRepository.save(customer2);
            entityManager.flush();
            entityManager.clear();

            List<Customer> customers = customerRepository.findAllWithProfiles();

            // Filter to only our test customers to avoid counting others
            List<Customer> ourTestCustomers = customers.stream()
                .filter(c -> c.getEmail().contains(String.valueOf(customerCounter)))
                .toList();

            assertThat(ourTestCustomers).hasSize(2);

            // Verify profiles are loaded
            Customer customerWithProfile = ourTestCustomers.stream()
                .filter(c -> c.getEmail().equals(testCustomer.getEmail()))
                .findFirst()
                .orElseThrow();
            assertThat(customerWithProfile.getCustomerProfile()).isNotNull();
            assertThat(customerWithProfile.getCustomerProfile().getAddress()).isEqualTo("123 Main St");

            Customer customerWithoutProfile = ourTestCustomers.stream()
                .filter(c -> c.getEmail().equals(customer2.getEmail()))
                .findFirst()
                .orElseThrow();
            assertThat(customerWithoutProfile.getCustomerProfile()).isNull();
        }

        @Test
        @DisplayName("Should find all customers with profiles using pagination")
        void shouldFindAllWithProfilesUsingPagination() {
            // Count existing customers first
            long initialCount = customerRepository.count();

            Customer customer2 = new Customer();
            customer2.setFirstName("Jane");
            customer2.setLastName("Smith");
            customer2.setEmail("jane.smith" + customerCounter + "@example.com");

            Customer customer3 = new Customer();
            customer3.setFirstName("Bob");
            customer3.setLastName("Johnson");
            customer3.setEmail("bob.johnson" + customerCounter + "@example.com");

            customerRepository.save(testCustomer);
            customerRepository.save(customer2);
            customerRepository.save(customer3);
            entityManager.flush();
            entityManager.clear();

            // Get first page with size 2
            Pageable pageable = PageRequest.of(0, 2);
            Page<Customer> customerPage = customerRepository.findAllWithProfiles(pageable);

            // Verify pagination behavior
            assertThat(customerPage.getContent()).hasSize(2); // Page should have 2 items
            assertThat(customerPage.getSize()).isEqualTo(2); // Page size should be 2
            assertThat(customerPage.getNumber()).isZero(); // Should be first page
            assertThat(customerPage.getTotalElements()).isEqualTo(initialCount + 3); // Total should be initial + our 3

            // Calculate expected total pages
            long expectedTotalPages = (long) Math.ceil((double)(initialCount + 3) / 2);
            assertThat(customerPage.getTotalPages()).isEqualTo((int)expectedTotalPages);

            // Should have next page if total > 2
            if (initialCount + 3 > 2) {
                assertThat(customerPage.hasNext()).isTrue();
            }

            // Verify that profiles are loaded for returned customers (no lazy loading exceptions)
            for (Customer customer : customerPage.getContent()) {
                // This should not throw LazyInitializationException if fetch join works
                CustomerProfile profile = customer.getCustomerProfile();
                // Profile might be null for some customers, that's OK
                if (profile != null) {
                    assertThat(profile.getAddress()).isNotNull();
                }
            }
        }

        @Test
        @DisplayName("Should delete customer by ID using standard JPA delete")
        void shouldDeleteCustomerUsingStandardJpaDelete() {
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            Long customerId = savedCustomer.getId();
            Long profileId = savedCustomer.getCustomerProfile().getId();

            // Use standard JPA deleteById which should trigger cascade properly
            customerRepository.deleteById(customerId);
            entityManager.flush();
            entityManager.clear();

            assertThat(customerRepository.findById(customerId)).isEmpty();

            // Profile should be deleted due to JPA cascade (orphanRemoval = true)
            CustomerProfile remainingProfile = entityManager.find(CustomerProfile.class, profileId);
            assertThat(remainingProfile).isNull();
        }

        @Test
        @DisplayName("Should return 0 when deleting non-existent customer")
        void shouldReturn0WhenDeletingNonExistentCustomer() {
            int deletedCount = customerRepository.deleteByCustomerId(999L);

            assertThat(deletedCount).isZero();
        }
    }

    @Nested
    @DisplayName("Service Package Relationship Tests")
    class ServicePackageRelationshipTests {

        @Test
        @DisplayName("Should find customer with profile and service packages")
        void shouldFindWithProfileAndServicePackages() {
            entityManager.persist(testServicePackage);
            testCustomer.getSubscribedPackages().add(testServicePackage);
            testServicePackage.getSubscribers().add(testCustomer);
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            entityManager.clear();

            Optional<Customer> result = customerRepository.findByIdWithProfileAndServicePackages(savedCustomer.getId());

            assertThat(result).isPresent();
            Customer foundCustomer = result.get();

            // Verify profile is loaded
            assertThat(foundCustomer.getCustomerProfile()).isNotNull();
            assertThat(foundCustomer.getCustomerProfile().getAddress()).isEqualTo("123 Main St");

            // Verify service packages are loaded
            assertThat(foundCustomer.getSubscribedPackages()).hasSize(1);
            ServicePackage loadedPackage = foundCustomer.getSubscribedPackages().iterator().next();
            assertThat(loadedPackage.getName()).isEqualTo(testServicePackage.getName()); // Use the actual name
        }

        @Test
        @DisplayName("Should find all customers with profiles and service packages")
        void shouldFindAllWithProfilesAndServicePackages() {
            // Create fresh service package and customers for this test
            ServicePackage freshServicePackage = new ServicePackage();
            freshServicePackage.setName("Test Package " + customerCounter);
            freshServicePackage.setDescription("Test package");
            freshServicePackage.setMonthlyPrice(new BigDecimal("19.99"));
            freshServicePackage.setActive(true);
            freshServicePackage.setSubscribers(new HashSet<>());
            entityManager.persist(freshServicePackage);

            // Create customer with package
            Customer customerWithPackage = new Customer();
            customerWithPackage.setFirstName("TestFirst");
            customerWithPackage.setLastName("WithPackage");
            customerWithPackage.setEmail("test.with.package" + customerCounter + "@example.com");
            customerWithPackage.setSubscribedPackages(new HashSet<>());
            customerWithPackage.getSubscribedPackages().add(freshServicePackage);
            freshServicePackage.getSubscribers().add(customerWithPackage);

            // Create customer without package
            Customer customerWithoutPackage = new Customer();
            customerWithoutPackage.setFirstName("TestFirst");
            customerWithoutPackage.setLastName("WithoutPackage");
            customerWithoutPackage.setEmail("test.without.package" + customerCounter + "@example.com");
            customerWithoutPackage.setSubscribedPackages(new HashSet<>());

            customerRepository.save(customerWithPackage);
            customerRepository.save(customerWithoutPackage);
            entityManager.flush();
            entityManager.clear();

            List<Customer> allCustomers = customerRepository.findAllWithProfilesAndServicePackages();

            // Filter to only our test customers
            List<Customer> testCustomers = allCustomers.stream()
                .filter(c -> c.getEmail().startsWith("test."))
                .toList();

            // Should have 2 test customers
            assertThat(testCustomers).hasSize(2);

            Customer foundCustomerWithPackage = testCustomers.stream()
                .filter(c -> c.getEmail().contains("with.package"))
                .findFirst()
                .orElseThrow();
            assertThat(foundCustomerWithPackage.getSubscribedPackages()).hasSize(1);

            Customer foundCustomerWithoutPackage = testCustomers.stream()
                .filter(c -> c.getEmail().contains("without.package"))
                .findFirst()
                .orElseThrow();
            assertThat(foundCustomerWithoutPackage.getSubscribedPackages()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Optimistic Locking Tests")
    class OptimisticLockingTests {

        @Test
        @DisplayName("Should increment version on update")
        void shouldIncrementVersionOnUpdate() {
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            Long initialVersion = savedCustomer.getVersion();

            savedCustomer.setFirstName("Jane");
            Customer updatedCustomer = customerRepository.save(savedCustomer);
            entityManager.flush();

            assertThat(updatedCustomer.getVersion()).isEqualTo(initialVersion + 1);
        }

        @Test
        @DisplayName("Should handle optimistic locking conflict")
        void shouldHandleOptimisticLockingConflict() {
            // Save customer and get initial state
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            entityManager.clear(); // Clear after initial save

            Long customerId = savedCustomer.getId();

            // Load two separate instances representing different sessions
            Customer customer1 = customerRepository.findById(customerId).orElseThrow();
            Customer customer2 = customerRepository.findById(customerId).orElseThrow();

            // Detach customer2 from persistence context to simulate different session
            entityManager.detach(customer2);
            Long customer2StaleVersion = customer2.getVersion(); // Remember the stale version

            // First update succeeds
            customer1.setFirstName("Jane");
            Customer updatedCustomer1 = customerRepository.save(customer1);
            entityManager.flush();

            // Verify first update incremented version
            assertThat(updatedCustomer1.getVersion()).isEqualTo(customer2StaleVersion + 1);

            // Second update with stale version should fail
            customer2.setFirstName("Bob");
            // Ensure customer2 still has the stale version
            assertThat(customer2.getVersion()).isEqualTo(customer2StaleVersion);

            // This should throw optimistic locking exception
            try {
                customerRepository.save(customer2);
                entityManager.flush();
                assertThat(false).as("Should have thrown optimistic locking exception").isTrue();
            } catch (Exception e) {
                // Check for various possible exception messages
                String message = e.getMessage().toLowerCase();
                assertThat(message).containsAnyOf("optimistic", "version", "stale", "concurrent", "row was updated", "object optimistic locking failed");
            }
        }
    }

    @Nested
    @DisplayName("Cascade Operations Tests")
    class CascadeOperationsTests {

        @Test
        @DisplayName("Should cascade delete profile when customer is deleted")
        void shouldCascadeDeleteProfile() {
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();
            Long customerId = savedCustomer.getId();
            Long profileId = savedCustomer.getCustomerProfile().getId();

            customerRepository.deleteById(customerId);
            entityManager.flush();

            assertThat(entityManager.find(Customer.class, customerId)).isNull();
            assertThat(entityManager.find(CustomerProfile.class, profileId)).isNull();
        }

        @Test
        @DisplayName("Should cascade persist profile when customer is saved")
        void shouldCascadePersistProfile() {
            Customer savedCustomer = customerRepository.save(testCustomer);
            entityManager.flush();

            assertThat(savedCustomer.getId()).isNotNull();
            assertThat(savedCustomer.getCustomerProfile().getId()).isNotNull();

            // Verify both entities are persisted
            Customer foundCustomer = entityManager.find(Customer.class, savedCustomer.getId());
            CustomerProfile foundProfile = entityManager.find(CustomerProfile.class, savedCustomer.getCustomerProfile().getId());

            assertThat(foundCustomer).isNotNull();
            assertThat(foundProfile).isNotNull();
            assertThat(foundProfile.getCustomer().getId()).isEqualTo(foundCustomer.getId());
        }
    }
}