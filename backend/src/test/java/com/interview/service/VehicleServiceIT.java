package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.exception.DuplicateVinException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class VehicleServiceIT {

    @Autowired VehicleService service;
    @Autowired VehicleRepository repo;

    @Test
    void update_shouldThrowDuplicateVinException() {
        Vehicle v1 = new Vehicle();
        v1.setVin("11111111111111111");
        v1.setMake("Toyota");
        v1.setModel("Camry");
        v1.setYear(2020);
        repo.save(v1);

        Vehicle v2 = new Vehicle();
        v2.setVin("22222222222222222");
        v2.setMake("Honda");
        v2.setModel("Civic");
        v2.setYear(2021);
        v2 = repo.save(v2);

        VehicleRequest req = new VehicleRequest(
                "11111111111111111",
                "Honda",
                "Civic",
                2021
        );

        Vehicle finalV = v2;
        assertThatThrownBy(() -> service.update(finalV.getId(), req))
                .isInstanceOf(DuplicateVinException.class);
    }
}
