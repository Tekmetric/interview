package com.interview.controller;

import com.interview.dto.VehicleResponse;
import com.interview.dto.VehicleSearchCriteria;
import com.interview.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    @Test
    void getVehiclesRejectsPageSizesAboveMaximum() {
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        Pageable pageable = PageRequest.of(0, 51);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleController.getVehicles(criteria, pageable)
        );

        assertEquals("Page size must not exceed 50", exception.getMessage());
        verifyNoInteractions(vehicleService);
    }

    @Test
    void getVehiclesAllowsPageSizeAtMaximumBoundary() {
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        Pageable pageable = PageRequest.of(0, 50);
        when(vehicleService.findAll(same(criteria), same(pageable))).thenReturn(Page.empty(pageable));

        Page<VehicleResponse> result = vehicleController.getVehicles(criteria, pageable);

        assertTrue(result.isEmpty());
        verify(vehicleService).findAll(criteria, pageable);
    }
}
