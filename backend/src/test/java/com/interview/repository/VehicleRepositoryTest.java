package com.interview.repository;

import com.interview.config.JpaConfig;
import com.interview.model.Vehicle;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@TestPropertySource(
        properties = {
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.sql.init.mode=never"
        })
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    private static Vehicle vehicle(
            String make, String model, int year, String vin, int mileage) {
        Vehicle v = new Vehicle();
        v.setId(UUID.randomUUID());
        v.setMake(make);
        v.setModel(model);
        v.setYear(year);
        v.setVin(vin);
        v.setMileage(mileage);
        return v;
    }

    @Test
    void shouldDetectExistingVinAfterPersist() {
        vehicleRepository.save(
                vehicle("Toyota", "Camry", 2020, "4T1B11HK0KU800099", 1000));

        assertThat(vehicleRepository.existsByVin("4T1B11HK0KU800099")).isTrue();
        assertThat(vehicleRepository.existsByVin("1HGBH41JXMN109186")).isFalse();
    }

    @Test
    void shouldFilterFindAllByMakeAndYear() {
        vehicleRepository.save(
                vehicle("Toyota", "Camry", 2020, "4T1B11HK0KU800001", 1000));
        vehicleRepository.save(
                vehicle("Honda", "Civic", 2020, "2HGFC2F59KH800002", 2000));
        vehicleRepository.save(
                vehicle("Honda", "Accord", 2021, "1HGCV1F30MA800003", 3000));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> honda2020 =
                vehicleRepository.findAll("Honda", 2020, pageable);
        assertThat(honda2020.getContent()).hasSize(1);
        assertThat(honda2020.getContent().get(0).getModel()).isEqualTo("Civic");

        Page<Vehicle> all = vehicleRepository.findAll(null, null, pageable);
        assertThat(all.getContent()).hasSize(3);
    }
}
