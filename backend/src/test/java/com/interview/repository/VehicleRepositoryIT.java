package com.interview.repository;

import com.interview.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class VehicleRepositoryIT {

    @Autowired VehicleRepository repo;

    @Test
    void existsByVinAndIdNot_shouldDetectDuplicates() {
        Vehicle v1 = new Vehicle();
        v1.setVin("12345678901234567");
        v1.setMake("Toyota");
        v1.setModel("Camry");
        v1.setYear(2020);
        repo.save(v1);

        boolean exists = repo.existsByVinAndIdNot("12345678901234567", 999L);

        assertThat(exists).isTrue();
    }
}
