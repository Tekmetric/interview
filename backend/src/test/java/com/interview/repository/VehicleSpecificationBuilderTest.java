package com.interview.repository;

import com.interview.config.TestAuditConfig;
import com.interview.domain.Vehicle;
import com.interview.domain.VehicleType;
import com.interview.filter.VehicleQueryFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;
import java.util.List;
import java.util.Set;

import static com.interview.domain.VehicleType.PICKUP;
import static com.interview.domain.VehicleType.SEDAN;
import static com.interview.domain.VehicleType.SUV;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestAuditConfig.class)
public class VehicleSpecificationBuilderTest {

    private VehicleSpecificationBuilder vehicleSpecificationBuilder;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle testVehicle1;
    private Vehicle testVehicle2;
    private Vehicle testVehicle3;

    @BeforeEach
    public void setUp() {
        vehicleSpecificationBuilder = new VehicleSpecificationBuilder();
        vehicleRepository.deleteAll();

        testVehicle1 = Vehicle.builder()
                .type(SEDAN)
                .productionYear(Year.of(2020))
                .vin("1HGBH41JXMN109186")
                .model("Corolla")
                .make("Toyota")
                .build();

        testVehicle2 = Vehicle.builder()
                .type(SUV)
                .productionYear(Year.of(2021))
                .vin("1HGBH41JXMN109187")
                .model("CR-V")
                .make("Honda")
                .build();

        testVehicle3 = Vehicle.builder()
                .type(PICKUP)
                .productionYear(Year.of(2019))
                .vin("1HGBH41JXMN109188")
                .model("F-150")
                .make("Ford")
                .build();

        testVehicle1 = vehicleRepository.save(testVehicle1);
        testVehicle2 = vehicleRepository.save(testVehicle2);
        testVehicle3 = vehicleRepository.save(testVehicle3);
    }

    @Test
    public void buildSpecification_NullFilter_AllDbEntries() {
        // Given
        final VehicleQueryFilter filter = null;

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).hasSize(3);
    }

    @Test
    public void buildSpecification_EmptyFilter_InternalServiceException() {
        // Given
        final VehicleQueryFilter filter = generateEmptyFilter();

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).hasSize(3);
    }

    @Test
    public void buildSpecification_IncludingVins_ExpectedEntities() {
        // Given
        final Set<String> includingVins = Set.of("1HGBH41JXMN109186", "1HGBH41JXMN109187");
        final VehicleQueryFilter filter = VehicleQueryFilter.builder()
                .includingVins(includingVins)
                .build();

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).hasSize(includingVins.size());
        assertThat(vehicles).extracting(Vehicle::getVin).containsAll(includingVins);
    }

    @Test
    public void buildSpecification_ExcludingVins_ExpectedEntities() {
        // Given
        final Set<String> excludingVins = Set.of("1HGBH41JXMN109187");
        final VehicleQueryFilter filter = VehicleQueryFilter.builder()
                .excludingVins(excludingVins)
                .build();

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).hasSize(2);
        assertThat(vehicles).extracting(Vehicle::getVin).containsAll(Set.of("1HGBH41JXMN109186", "1HGBH41JXMN109188"));
    }

    @Test
    public void buildSpecification_ExcludingVehicleTypes_ExpectedEntities() {
        // Given
        final Set<VehicleType> excludingTypes = Set.of(PICKUP);
        final VehicleQueryFilter filter = VehicleQueryFilter.builder()
                .excludingVehicleTypes(excludingTypes)
                .build();

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).isNotNull();
        assertThat(vehicles).hasSize(2);
        assertThat(vehicles).extracting(Vehicle::getType).containsAll(Set.of(SEDAN, SUV));
    }

    @Test
    public void buildSpecification_ExcludingVehicleTypesAndIncludingVins_ExpectedEntities() {
        // Given
        final VehicleQueryFilter filter = VehicleQueryFilter.builder()
                .excludingVehicleTypes(Set.of(PICKUP, SUV))
                .includingVins(Set.of("1HGBH41JXMN109187", "1HGBH41JXMN109186"))
                .build();

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).isNotNull();
        assertThat(vehicles).hasSize(1);
        assertThat(vehicles).extracting(Vehicle::getVin).containsExactly("1HGBH41JXMN109186");
    }

    @Test
    public void buildSpecification_ExcludingVinsAndProductionYearFrom_ExpectedEntities() {
        // Given
        final VehicleQueryFilter filter = VehicleQueryFilter.builder()
                .productionYearFrom(Year.of(2018))
                .excludingVins(Set.of("1HGBH41JXMN109187", "1HGBH41JXMN109186"))
                .build();

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).isNotNull();
        assertThat(vehicles).hasSize(1);
        assertThat(vehicles).extracting(Vehicle::getVin).containsExactly("1HGBH41JXMN109188");
    }

    @Test
    public void buildSpecification_IncludingVinsAndProductionYearTo_EmptyResult() {
        // Given
        final VehicleQueryFilter filter = VehicleQueryFilter.builder()
                .productionYearTo(Year.of(2020))
                .includingVins(Set.of("1HGBH41JXMN109187"))
                .build();

        // When
        final Specification<Vehicle> vehicleSpecification = vehicleSpecificationBuilder.buildSpecification(filter);
        final List<Vehicle> vehicles = vehicleRepository.findAll(vehicleSpecification);

        // Then
        assertThat(vehicles).isEmpty();
    }

    private static VehicleQueryFilter generateEmptyFilter() {
        return VehicleQueryFilter.builder()
                .includingIds(Set.of())
                .excludingIds(Set.of())
                .includingVehicleTypes(Set.of())
                .excludingVehicleTypes(Set.of())
                .includingVins(Set.of())
                .excludingVins(Set.of())
                .build();
    }

}
