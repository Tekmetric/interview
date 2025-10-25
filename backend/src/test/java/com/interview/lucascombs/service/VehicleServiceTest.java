package com.interview.lucascombs.service;


import com.interview.lucascombs.dao.VehicleDao;
import com.interview.lucascombs.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    VehicleDao vehicleDao;

    @Mock
    Vehicle vehicle;

    @InjectMocks
    VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById() {
        doReturn(vehicle).when(vehicleDao).getOne(5L);

        assertSame(vehicle, vehicleService.getById(5L).orElse(null));

        verify(vehicleDao, times(1)).getOne(5L);
    }

    @Test
    void testGetAllNullArguments() {
        final Page<Vehicle> expectedResult = mock(Page.class);
        final ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        doReturn(expectedResult).when(vehicleDao).findAll(any(Pageable.class));

        assertSame(expectedResult, vehicleService.getAll(null, null));

        verify(vehicleDao, times(1)).findAll(pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void testGetAllWithArguments() {
        final Page<Vehicle> expectedResult = mock(Page.class);
        final ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        doReturn(expectedResult).when(vehicleDao).findAll(any(Pageable.class));

        assertSame(expectedResult, vehicleService.getAll(5, 7));

        verify(vehicleDao, times(1)).findAll(pageableCaptor.capture());
        assertEquals(5, pageableCaptor.getValue().getPageNumber());
        assertEquals(7, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void testDeleteById() {
        vehicleService.deleteById(5L);
        verify(vehicleDao, times(1)).deleteById(5L);
    }

    @Test
    void testSave() {
        vehicleService.save(vehicle);
        verify(vehicleDao, times(1)).saveAndFlush(vehicle);
    }
}
