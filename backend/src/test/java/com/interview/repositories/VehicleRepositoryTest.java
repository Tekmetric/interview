package com.interview.repositories;

import com.interview.config.TestAuditingConfiguration;
import com.interview.domain.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestAuditingConfiguration.class)
class VehicleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle vehicle1;
    private Vehicle vehicle2;

    @BeforeEach
    void setUp() {
        vehicle1 = Vehicle.builder()
                .vin("1HGBH41JXMN109186")
                .make("Honda")
                .model("Civic")
                .ownerName("Logan")
                .build();

        vehicle2 = Vehicle.builder()
                .vin("2T1BURHE0JC014702")
                .make("Toyota")
                .model("Corolla")
                .ownerName("Mike")
                .build();

        vehicle1 = entityManager.persistAndFlush(vehicle1);
        vehicle2 = entityManager.persistAndFlush(vehicle2);
    }

    @Test
    void findByVin_WhenVinExists_ShouldReturnVehicle() {
        String existingVin = "1HGBH41JXMN109186";

        Optional<Vehicle> result = vehicleRepository.findByVin(existingVin);

        assertThat(result).isPresent();
        assertThat(result.get().getVin()).isEqualTo(existingVin);
        assertThat(result.get().getMake()).isEqualTo("Honda");
    }

    @Test
    void findByVin_WhenVinDoesNotExist_ShouldReturnEmpty() {
        String nonExistentVin = "NONEXISTENT123456";
        Optional<Vehicle> result = vehicleRepository.findByVin(nonExistentVin);
        assertThat(result).isEmpty();
    }

    @Test
    void findByVin_IsCaseSensitive() {
        String vinLowerCase = "1hgbh41jxmn109186";
        Optional<Vehicle> result = vehicleRepository.findByVin(vinLowerCase);
        assertThat(result).isEmpty(); // Should not find uppercase VIN with lowercase search
    }

    @Test
    void findByVin_WithNullVin_ShouldReturnEmpty() {
        Optional<Vehicle> result = vehicleRepository.findByVin(null);
        assertThat(result).isEmpty();
    }

    @Test
    void existsByVinAndIdNot_WhenVinExistsWithDifferentId_ShouldReturnTrue() {
        String existingVin = "1HGBH41JXMN109186";
        Long differentId = vehicle2.getId();
        boolean exists = vehicleRepository.existsByVinAndIdNot(existingVin, differentId);
        assertThat(exists).isTrue();
    }

    @Test
    void existsByVinAndIdNot_WhenVinExistsWithSameId_ShouldReturnFalse() {
        String existingVin = "1HGBH41JXMN109186";
        Long sameId = vehicle1.getId();
        boolean exists = vehicleRepository.existsByVinAndIdNot(existingVin, sameId);
        assertThat(exists).isFalse();
    }

    @Test
    void existsByVinAndIdNot_WhenVinDoesNotExist_ShouldReturnFalse() {
        String nonExistentVin = "NONEXISTENT123456";
        Long anyId = vehicle1.getId();
        boolean exists = vehicleRepository.existsByVinAndIdNot(nonExistentVin, anyId);
        assertThat(exists).isFalse();
    }

    @Test
    void existsByVinAndIdNot_WithNullVin_ShouldReturnFalse() {
        Long anyId = vehicle1.getId();
        boolean exists = vehicleRepository.existsByVinAndIdNot(null, anyId);
        assertThat(exists).isFalse();
    }

    @Test
    void existsByVinAndIdNot_WithNullId_ShouldReturnTrue() {
        String existingVin = "1HGBH41JXMN109186";
        boolean exists = vehicleRepository.existsByVinAndIdNot(existingVin, null);
        assertThat(exists).isTrue();
    }

    @Test
    void existsByVinAndIdNot_MultipleVehiclesScenario() {
        // Test scenario: checking if VIN exists for vehicles other than the current one
        // This is the typical use case during updates

        // update vehicle1 with vehicle2's VIN (should conflict)
        String vehicle2Vin = vehicle2.getVin();
        Long vehicle1Id = vehicle1.getId();
        boolean conflictExists = vehicleRepository.existsByVinAndIdNot(vehicle2Vin, vehicle1Id);
        assertThat(conflictExists).isTrue();

        // update vehicle2 with its own VIN (should be allowed)
        Long vehicle2Id = vehicle2.getId();
        boolean selfUpdate = vehicleRepository.existsByVinAndIdNot(vehicle2Vin, vehicle2Id);
        assertThat(selfUpdate).isFalse();
    }
}