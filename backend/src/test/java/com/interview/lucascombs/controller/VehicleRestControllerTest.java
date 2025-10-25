package com.interview.lucascombs.controller;

import com.interview.lucascombs.entity.Vehicle;
import com.interview.lucascombs.mapping.VehicleResourceMapper;
import com.interview.lucascombs.resource.VehicleResource;
import com.interview.lucascombs.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class VehicleRestControllerTest {

    @Mock
    VehicleService vehicleService;

    @Mock
    VehicleResourceMapper vehicleResourceMapper;

    @Mock
    VehicleResource vehicleResource;

    @Mock
    Vehicle vehicle;

    @InjectMocks
    VehicleRestController vehicleRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doReturn(vehicleResource).when(vehicleResourceMapper).toResource(eq(vehicle));
        doReturn(vehicle).when(vehicleResourceMapper).toEntity(eq(vehicleResource));
    }

    @Test
    void testGetAll() {
        final Page<Vehicle> serviceCallResult = new PageImpl<Vehicle>(Collections.singletonList(vehicle), Pageable.unpaged(), 1);
        doReturn(serviceCallResult).when(vehicleService).getAll(eq(7), eq(10));

        final ResponseEntity<List<VehicleResource>> result = vehicleRestController.getAll(7, 10);

        assertEquals(1, result.getBody().size());
        assertSame(vehicleResource, result.getBody().get(0));
        verify(vehicleService, times(1)).getAll(eq(7), eq(10));
    }

    @Test
    void testGetById() {
        doReturn(Optional.of(vehicle)).when(vehicleService).getById(eq(5L));

        assertSame(vehicleRestController.getById(5L), vehicleResource);
        verify(vehicleService, times(1)).getById(eq(5L));
    }

    @Test
    void testDeleteById() {
        final ResponseEntity<?> result = vehicleRestController.deleteById(5L);
        verify(vehicleService, times(1)).deleteById(eq(5L));
        assertEquals(204, result.getStatusCodeValue());
    }

    @Test
    void testCreate() {
        doReturn(vehicle).when(vehicleService).save(eq(vehicle));

        final ResponseEntity<VehicleResource> result = vehicleRestController.create(vehicleResource);

        verify(vehicleService, times(1)).save(eq(vehicle));
        assertSame(vehicleResource, result.getBody());
    }

    @Test
    void testUpdate() {
        doReturn(Optional.of(vehicle)).when(vehicleService).getById(eq(5L));
        doReturn(vehicle).when(vehicleResourceMapper).updateEntity(eq(vehicleResource), eq(vehicle));
        doReturn(vehicle).when(vehicleService).save(eq(vehicle));

        final ResponseEntity<VehicleResource> result = vehicleRestController.update(5L, vehicleResource);

        verify(vehicleService, times(1)).getById(eq(5L));
        verify(vehicleResourceMapper, times(1)).updateEntity(eq(vehicleResource), eq(vehicle));
        verify(vehicleService, times(1)).save(eq(vehicle));
        assertSame(vehicleResource, result.getBody());
        assertEquals(200, result.getStatusCodeValue());
    }
}
