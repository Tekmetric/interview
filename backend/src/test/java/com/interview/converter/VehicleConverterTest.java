package com.interview.converter;

import com.interview.domain.Vehicle;
import com.interview.dto.VehicleRequest;
import com.interview.test.data.VehicleTestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VehicleConverterTest {

    @Test
    void shouldConvertToVehicle() {
        // given
        VehicleRequest vehicleRequest = VehicleTestData.vehicleRequest();
        Vehicle expectedVehicle = VehicleTestData.vehicle();

        // then
        Vehicle vehicle = VehicleConverter.toVehicle(vehicleRequest);
        assertEquals(expectedVehicle, vehicle);
    }
}
