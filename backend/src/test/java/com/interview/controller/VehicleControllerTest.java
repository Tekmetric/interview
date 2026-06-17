package com.interview.controller;

import com.interview.dto.PageResponse;
import com.interview.dto.VehicleResponse;
import com.interview.dto.VehicleSearchCriteria;
import com.interview.entity.UserRole;
import com.interview.security.AuthenticatedUser;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {
    private static final AuthenticatedUser OWNER = new AuthenticatedUser(2L, "owner1@example.com", UserRole.VEHICLE_OWNER);

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
                () -> vehicleController.getVehicles(criteria, pageable, OWNER)
        );

        assertEquals("Page size must not exceed 50", exception.getMessage());
        verifyNoInteractions(vehicleService);
    }

    @Test
    void getVehiclesWrapsResultsInStablePageResponse() {
        VehicleSearchCriteria criteria = new VehicleSearchCriteria();
        Pageable pageable = PageRequest.of(0, 50);
        when(vehicleService.findAll(same(criteria), same(pageable), same(OWNER))).thenReturn(Page.empty(pageable));

        PageResponse<VehicleResponse> result = vehicleController.getVehicles(criteria, pageable, OWNER);

        assertTrue(result.items().isEmpty());
        assertEquals(0, result.page());
        assertEquals(50, result.size());
        assertEquals(0, result.itemCount());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        assertTrue(result.first());
        assertTrue(result.last());
        verify(vehicleService).findAll(criteria, pageable, OWNER);
        verifyNoMoreInteractions(vehicleService);
    }
}

