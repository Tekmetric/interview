package com.interview.repository;

import com.interview.config.TestJpaConfig;
import com.interview.entity.Customer;
import com.interview.entity.CustomerProfile;
import com.interview.entity.Vehicle;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@Transactional
@Rollback
@DisplayName("VehicleRepository Integration Tests")
class VehicleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Vehicle testVehicle;
    private Customer testCustomer;
    private CustomerProfile testProfile;
    private static int vehicleCounter = 0;

    @BeforeEach
    void setUp() {
        // Increment counter to ensure unique data across tests
        vehicleCounter++;

        // Create test customer with profile
        testCustomer = new Customer();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe" + vehicleCounter + "@example.com");
        testCustomer.setPhone("+1-555-0101");

        testProfile = new CustomerProfile();
        testProfile.setAddress("123 Main St");
        testProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testProfile.setPreferredContactMethod(ContactMethod.EMAIL);
        testProfile.setCustomer(testCustomer);
        testCustomer.setCustomerProfile(testProfile);

        // Save customer first
        testCustomer = customerRepository.save(testCustomer);

        // Create test vehicle
        testVehicle = new Vehicle();
        testVehicle.setCustomer(testCustomer);
        testVehicle.setVin("1HGCM82633A" + String.format("%06d", vehicleCounter)); // Unique VIN
        testVehicle.setMake("Honda");
        testVehicle.setModel("Accord");
        testVehicle.setYear(2020);
    }

    @Nested
    @DisplayName("Basic Repository Operations")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should save vehicle with customer relationship")
        void shouldSaveVehicleWithCustomer() {
            Vehicle savedVehicle = vehicleRepository.save(testVehicle);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedVehicle.getId()).isNotNull();
            assertThat(savedVehicle.getCustomer()).isNotNull();
            assertThat(savedVehicle.getCustomer().getId()).isEqualTo(testCustomer.getId());

            Vehicle foundVehicle = entityManager.find(Vehicle.class, savedVehicle.getId());
            assertThat(foundVehicle).isNotNull();
            assertThat(foundVehicle.getVin()).isEqualTo(testVehicle.getVin());
        }

        @Test
        @DisplayName("Should enforce VIN uniqueness constraint")
        void shouldEnforceVinUniqueness() {
            vehicleRepository.save(testVehicle);
            entityManager.flush();

            Vehicle duplicateVehicle = new Vehicle();
            duplicateVehicle.setCustomer(testCustomer);
            duplicateVehicle.setVin(testVehicle.getVin()); // Same VIN
            duplicateVehicle.setMake("Toyota");
            duplicateVehicle.setModel("Camry");
            duplicateVehicle.setYear(2021);

            try {
                vehicleRepository.save(duplicateVehicle);
                entityManager.flush();
                assertThat(false).as("Should have thrown constraint violation").isTrue();
            } catch (Exception e) {
                String message = e.getMessage().toLowerCase();
                assertThat(message).containsAnyOf("unique", "duplicate", "constraint", "violation");
            }
        }
    }

    @Nested
    @DisplayName("Custom Query Tests")
    class CustomQueryTests {

        @Test
        @DisplayName("Should check if vehicle exists by VIN")
        void shouldCheckExistsByVin() {
            vehicleRepository.save(testVehicle);
            entityManager.flush();

            assertThat(vehicleRepository.existsByVin(testVehicle.getVin())).isTrue();
            assertThat(vehicleRepository.existsByVin("NONEXISTENT123")).isFalse();
        }

        @Test
        @DisplayName("Should find vehicle by ID with customer and profile")
        void shouldFindByIdWithCustomer() {
            Vehicle savedVehicle = vehicleRepository.save(testVehicle);
            entityManager.flush();
            entityManager.clear(); // Clear to test fetch join

            Optional<Vehicle> result = vehicleRepository.findByIdWithCustomer(savedVehicle.getId());

            assertThat(result).isPresent();
            Vehicle foundVehicle = result.get();
            assertThat(foundVehicle.getVin()).isEqualTo(testVehicle.getVin());

            // Verify customer is loaded (no lazy loading exception)
            assertThat(foundVehicle.getCustomer()).isNotNull();
            assertThat(foundVehicle.getCustomer().getEmail()).isEqualTo(testCustomer.getEmail());

            // Verify customer profile is loaded
            assertThat(foundVehicle.getCustomer().getCustomerProfile()).isNotNull();
            assertThat(foundVehicle.getCustomer().getCustomerProfile().getAddress()).isEqualTo("123 Main St");
        }

        @Test
        @DisplayName("Should return empty when vehicle not found by ID")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Vehicle> result = vehicleRepository.findByIdWithCustomer(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should find all vehicles with customers")
        void shouldFindAllWithCustomers() {
            // Create vehicles with unique identifier
            String testId = "A" + (System.nanoTime() % 100000); // A for All vehicles test

            Vehicle vehicle1 = new Vehicle();
            vehicle1.setCustomer(testCustomer);
            vehicle1.setVin(testId + "00000000001");
            vehicle1.setMake("Honda");
            vehicle1.setModel("Accord");
            vehicle1.setYear(2020);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setCustomer(testCustomer);
            vehicle2.setVin(testId + "00000000002");
            vehicle2.setMake("Toyota");
            vehicle2.setModel("Corolla");
            vehicle2.setYear(2019);

            vehicleRepository.save(vehicle1);
            vehicleRepository.save(vehicle2);
            entityManager.flush();
            entityManager.clear();

            List<Vehicle> allVehicles = vehicleRepository.findAllWithCustomers();

            // Filter to only our test vehicles
            List<Vehicle> testVehicles = allVehicles.stream()
                .filter(v -> v.getVin().startsWith(testId))
                .toList();

            assertThat(testVehicles).hasSize(2);

            // Verify customers are loaded for our test vehicles
            for (Vehicle vehicle : testVehicles) {
                assertThat(vehicle.getCustomer()).isNotNull();
                assertThat(vehicle.getCustomer().getEmail()).isEqualTo(testCustomer.getEmail());
                assertThat(vehicle.getCustomer().getCustomerProfile()).isNotNull();
            }
        }

        @Test
        @DisplayName("Should find all vehicles with customers using pagination")
        void shouldFindAllWithCustomersUsingPagination() {
            // Count existing vehicles first
            long initialCount = vehicleRepository.count();

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setCustomer(testCustomer);
            vehicle2.setVin("2T1BURHE0JC" + String.format("%06d", vehicleCounter));
            vehicle2.setMake("Toyota");
            vehicle2.setModel("Corolla");
            vehicle2.setYear(2019);

            Vehicle vehicle3 = new Vehicle();
            vehicle3.setCustomer(testCustomer);
            vehicle3.setVin("3GNDA13D76S" + String.format("%06d", vehicleCounter));
            vehicle3.setMake("Chevrolet");
            vehicle3.setModel("Malibu");
            vehicle3.setYear(2018);

            vehicleRepository.save(testVehicle);
            vehicleRepository.save(vehicle2);
            vehicleRepository.save(vehicle3);
            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 2);
            Page<Vehicle> vehiclePage = vehicleRepository.findAllWithCustomers(pageable);

            assertThat(vehiclePage.getContent()).hasSize(2);
            assertThat(vehiclePage.getTotalElements()).isEqualTo(initialCount + 3);
            assertThat(vehiclePage.getSize()).isEqualTo(2);
            assertThat(vehiclePage.getNumber()).isZero();

            // Verify customers are loaded for returned vehicles
            for (Vehicle vehicle : vehiclePage.getContent()) {
                assertThat(vehicle.getCustomer()).isNotNull();
                // Customer profile should be loaded via fetch join
                if (vehicle.getCustomer().getCustomerProfile() != null) {
                    assertThat(vehicle.getCustomer().getCustomerProfile().getAddress()).isNotNull();
                }
            }
        }

        @Test
        @DisplayName("Should delete vehicle by ID")
        void shouldDeleteByVehicleId() {
            Vehicle savedVehicle = vehicleRepository.save(testVehicle);
            entityManager.flush();
            Long vehicleId = savedVehicle.getId();

            int deletedCount = vehicleRepository.deleteByVehicleId(vehicleId);
            entityManager.flush();
            entityManager.clear(); // Clear persistence context to force fresh query

            assertThat(deletedCount).isEqualTo(1);
            assertThat(vehicleRepository.findById(vehicleId)).isEmpty();
        }

        @Test
        @DisplayName("Should return 0 when deleting non-existent vehicle")
        void shouldReturn0WhenDeletingNonExistentVehicle() {
            int deletedCount = vehicleRepository.deleteByVehicleId(999L);

            assertThat(deletedCount).isZero();
        }
    }

    @Nested
    @DisplayName("Specification Query Tests")
    class SpecificationQueryTests {

        @Test
        @DisplayName("Should find vehicles using specification with pagination")
        void shouldFindVehiclesUsingSpecification() {
            // Use a short unique test identifier
            String testId = "P" + (System.nanoTime() % 100000); // P for pagination test

            Vehicle vehicle1 = new Vehicle();
            vehicle1.setCustomer(testCustomer);
            vehicle1.setVin(testId + "00000000001");
            vehicle1.setMake("Honda");
            vehicle1.setModel("Accord");
            vehicle1.setYear(2020);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setCustomer(testCustomer);
            vehicle2.setVin(testId + "00000000002");
            vehicle2.setMake("Toyota");
            vehicle2.setModel("Corolla");
            vehicle2.setYear(2019);

            vehicleRepository.save(vehicle1);
            vehicleRepository.save(vehicle2);
            entityManager.flush();
            entityManager.clear();

            // Create a specification to find only our Honda test vehicle
            Specification<Vehicle> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("make"), "Honda"),
                    criteriaBuilder.like(root.get("vin"), testId + "%")
                );

            Pageable pageable = PageRequest.of(0, 10);
            Page<Vehicle> result = vehicleRepository.findAll(spec, pageable);

            // Should find exactly our 1 Honda test vehicle
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().getMake()).isEqualTo("Honda");
            assertThat(result.getContent().getFirst().getVin()).startsWith(testId);

            // Verify EntityGraph worked - customer should be loaded
            Vehicle hondaVehicle = result.getContent().getFirst();
            assertThat(hondaVehicle.getCustomer()).isNotNull();
            assertThat(hondaVehicle.getCustomer().getEmail()).isEqualTo(testCustomer.getEmail());
            assertThat(hondaVehicle.getCustomer().getCustomerProfile()).isNotNull();
        }

        @Test
        @DisplayName("Should find vehicles using specification without pagination")
        void shouldFindVehiclesUsingSpecificationWithoutPagination() {
            // Use a short unique test identifier that fits in VIN length
            String testId = "T" + (System.nanoTime() % 100000); // T + 5 digits = 6 chars max

            Vehicle vehicle1 = new Vehicle();
            vehicle1.setCustomer(testCustomer);
            vehicle1.setVin(testId + "00000000001"); // Pad to make it 17 chars
            vehicle1.setMake("Honda");
            vehicle1.setModel("Accord");
            vehicle1.setYear(2020);

            Vehicle vehicle2 = new Vehicle();
            vehicle2.setCustomer(testCustomer);
            vehicle2.setVin(testId + "00000000002"); // Pad to make it 17 chars
            vehicle2.setMake("Toyota");
            vehicle2.setModel("Corolla");
            vehicle2.setYear(2019);

            vehicleRepository.save(vehicle1);
            vehicleRepository.save(vehicle2);
            entityManager.flush();
            entityManager.clear();

            // Create a specification that targets only our test vehicles
            Specification<Vehicle> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("year"), 2019),
                    criteriaBuilder.like(root.get("vin"), testId + "%")
                );

            List<Vehicle> result = vehicleRepository.findAll(spec);

            // Should find exactly 2 test vehicles
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(v -> v.getVin().startsWith(testId));

            // Verify EntityGraph worked for all vehicles
            for (Vehicle vehicle : result) {
                assertThat(vehicle.getCustomer()).isNotNull();
                assertThat(vehicle.getCustomer().getCustomerProfile()).isNotNull();
                assertThat(vehicle.getYear()).isGreaterThanOrEqualTo(2019);
            }
        }

        @Test
        @DisplayName("Should handle empty results with specifications")
        void shouldHandleEmptyResultsWithSpecifications() {
            vehicleRepository.save(testVehicle);
            entityManager.flush();

            // Create a specification that matches no vehicles
            Specification<Vehicle> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("make"), "NonExistentMake");

            List<Vehicle> result = vehicleRepository.findAll(spec);

            List<Vehicle> ourVehicles = result.stream()
                .filter(v -> v.getVin().contains(String.valueOf(vehicleCounter)))
                .toList();

            assertThat(ourVehicles).isEmpty();
        }
    }

    @Nested
    @DisplayName("Entity Relationship Tests")
    class EntityRelationshipTests {

        @Test
        @DisplayName("Should maintain customer relationship after vehicle update")
        void shouldMaintainCustomerRelationship() {
            Vehicle savedVehicle = vehicleRepository.save(testVehicle);
            entityManager.flush();

            savedVehicle.setYear(2021);
            savedVehicle.setModel("Accord Sport");
            Vehicle updatedVehicle = vehicleRepository.save(savedVehicle);
            entityManager.flush();
            entityManager.clear();

            Vehicle foundVehicle = vehicleRepository.findByIdWithCustomer(updatedVehicle.getId()).orElseThrow();
            assertThat(foundVehicle.getYear()).isEqualTo(2021);
            assertThat(foundVehicle.getModel()).isEqualTo("Accord Sport");
            assertThat(foundVehicle.getCustomer()).isNotNull();
            assertThat(foundVehicle.getCustomer().getId()).isEqualTo(testCustomer.getId());
        }

        @Test
        @DisplayName("Should handle vehicle without customer profile")
        void shouldHandleVehicleWithoutCustomerProfile() {
            // Create customer without profile
            Customer customerWithoutProfile = new Customer();
            customerWithoutProfile.setFirstName("Jane");
            customerWithoutProfile.setLastName("Smith");
            customerWithoutProfile.setEmail("jane.smith" + vehicleCounter + "@example.com");
            customerWithoutProfile = customerRepository.save(customerWithoutProfile);

            Vehicle vehicleWithoutProfile = new Vehicle();
            vehicleWithoutProfile.setCustomer(customerWithoutProfile);
            vehicleWithoutProfile.setVin("NOPR0FILE" + String.format("%06d", vehicleCounter));
            vehicleWithoutProfile.setMake("Ford");
            vehicleWithoutProfile.setModel("Focus");
            vehicleWithoutProfile.setYear(2020);

            Vehicle savedVehicle = vehicleRepository.save(vehicleWithoutProfile);
            entityManager.flush();
            entityManager.clear();

            Vehicle foundVehicle = vehicleRepository.findByIdWithCustomer(savedVehicle.getId()).orElseThrow();
            assertThat(foundVehicle.getCustomer()).isNotNull();
            assertThat(foundVehicle.getCustomer().getCustomerProfile()).isNull();
        }
    }
}