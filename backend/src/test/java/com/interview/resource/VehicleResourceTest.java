package com.interview.resource;

import static com.interview.VehicleFixture.LICENSE_PLATE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.interview.VehicleFixture;
import com.interview.resource.dto.VehicleDto;
import com.interview.resource.dto.VehicleUpdateRequest;
import com.interview.service.VehicleService;

public class VehicleResourceTest {

    private VehicleService vehicleService;

    private VehicleResource resource;

    @BeforeEach
    void setUp() {
        vehicleService = mock(VehicleService.class);
        resource = new VehicleResource(vehicleService);
    }

    @Test
    public void getVehiclesShouldReturnPageOfVehicles() {
        when(vehicleService.getVehicles(any()))
            .thenReturn(VehicleFixture.dtoWithPage());

        ResponseEntity<Page<VehicleDto>> result = 
            resource.getVehicles(0, 10, "createdAt", "ASC");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, result.getBody().getContent().size());
    }

    @Test
    public void getVehicleShouldReturnVehicleDto() {
        when(vehicleService.getVehicle(anyLong()))
            .thenReturn(VehicleFixture.dtoWithLicensePlate(LICENSE_PLATE));

        ResponseEntity<VehicleDto> result = resource.getVehicle(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(LICENSE_PLATE, result.getBody().getLicensePlate());
    }

    @Test
    public void updateVehicleShouldUpdateVehicle() {
        VehicleUpdateRequest request = VehicleUpdateRequest.builder()
            .licensePlate(LICENSE_PLATE).build();

        when(vehicleService.updateVehicle(1L, request))
            .thenReturn(VehicleFixture.dtoWithId(1L));
        
        ResponseEntity<VehicleDto> result = resource.updateVehicle(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(LICENSE_PLATE, result.getBody().getLicensePlate());
    }

    @Test
    public void deleteVehicleShouldDeleteVehicle() {
        when(vehicleService.deleteVehicle(1L)).thenReturn(1L);
        
        ResponseEntity<Long> result = resource.deleteVehicle(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody().longValue());
    }
}
