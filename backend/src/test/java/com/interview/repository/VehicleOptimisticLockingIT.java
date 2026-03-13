package com.interview.repository;

import com.interview.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class VehicleOptimisticLockingIT {

    @Autowired
    private VehicleRepository repo;

    @Test
    void optimisticLocking_shouldThrowException_whenConcurrentUpdatesOccur() {
        // Create initial entity
        Vehicle v = new Vehicle();
        v.setVin("12345678901234567");
        v.setMake("Toyota");
        v.setModel("Camry");
        v.setYear(2020);
        v = repo.saveAndFlush(v);

        // Load entity twice (simulating two concurrent transactions)
        Vehicle tx1 = repo.findById(v.getId()).orElseThrow();
        Vehicle tx2 = repo.findById(v.getId()).orElseThrow();

        // First transaction updates successfully
        tx1.setMake("Honda");
        repo.saveAndFlush(tx1);

        // Second transaction tries to update stale version
        tx2.setMake("Ford");

        assertThatThrownBy(() -> repo.saveAndFlush(tx2))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
