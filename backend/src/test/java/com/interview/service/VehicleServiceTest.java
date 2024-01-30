package com.interview.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import com.interview.VehicleFixture;
import com.interview.exception.LicensePlateExistException;
import com.interview.exception.VehicleNotFoundException;

import static com.interview.VehicleFixture.LICENSE_PLATE;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import com.interview.resource.dto.VehicleCreationRequest;
import com.interview.resource.dto.VehicleDto;
import com.interview.resource.dto.VehicleUpdateRequest;

public class VehicleServiceTest {

    private VehicleMapper vehicleMapper;

    private VehicleRepository vehicleRepository;

    private VehicleService service;

    @BeforeEach
    void setUp() {
        vehicleMapper = mock(VehicleMapper.class);
        vehicleRepository = mock(VehicleRepository.class);
        service = new VehicleService(vehicleMapper, vehicleRepository);
    }

    @Test
    public void getVehiclesShouldReturnPageOfVehicles() {
        PageRequest pageRequest = PageRequest.of(0, 10, Direction.ASC, "createdAt");

        when(vehicleRepository.findByDeletedAtIsNull(pageRequest))
            .thenReturn(VehicleFixture.withPage());
        when(vehicleMapper.toDto(any(Vehicle.class)))
            .thenReturn(VehicleFixture.dtoWithLicensePlate(LICENSE_PLATE));
        
        Page<VehicleDto> result = service.getVehicles(pageRequest);

        assertEquals(3, result.getContent().size());
    }

    @Test
    public void getVehiclesCanReturnEmptyPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(vehicleRepository.findByDeletedAtIsNull(pageRequest))
            .thenReturn(Page.empty());
        
        Page<VehicleDto> result = service.getVehicles(pageRequest);

        assertTrue(result.getContent().isEmpty());
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    public void getVehicleShouldReturnVehicleDto() {
        when(vehicleRepository.findByIdAndDeletedAtIsNull(anyLong()))
            .thenReturn(Optional.of(VehicleFixture.withLicensePlate(LICENSE_PLATE)));
        when(vehicleMapper.toDto(any(Vehicle.class)))
            .thenReturn(VehicleFixture.dtoWithLicensePlate(LICENSE_PLATE));

        VehicleDto result = service.getVehicle(1L);

        assertEquals(LICENSE_PLATE, result.getLicensePlate());
    }

    @Test
    public void getVehicleShouldThrowExceptionWhenVehicleNotFound() {
        when(vehicleRepository.findByIdAndDeletedAtIsNull(anyLong()))
            .thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> service.getVehicle(1L));
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    public void createVehicleShouldCreateVehicle() {
        VehicleCreationRequest request = VehicleCreationRequest.builder()
            .licensePlate(LICENSE_PLATE).build();

        when(vehicleRepository.findByLicensePlateAndDeletedAtIsNull(LICENSE_PLATE))
            .thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class)))
            .thenReturn(VehicleFixture.withLicensePlate(LICENSE_PLATE));
        when(vehicleMapper.toDomainEntity(any(VehicleCreationRequest.class)))
            .thenReturn(VehicleFixture.withLicensePlate(LICENSE_PLATE));
            when(vehicleMapper.toDto(any(Vehicle.class)))
            .thenReturn(VehicleFixture.dtoWithLicensePlate(LICENSE_PLATE));

        VehicleDto result = service.createVehicle(request);

        assertEquals(LICENSE_PLATE, result.getLicensePlate());
    }

    @Test
    public void createVehicleShouldThrowExceptionWhenLicensePlateExist() {
        VehicleCreationRequest request = VehicleCreationRequest.builder()
            .licensePlate(LICENSE_PLATE).build();
        
        when(vehicleRepository.findByLicensePlateAndDeletedAtIsNull(LICENSE_PLATE))
            .thenReturn(Optional.of(VehicleFixture.withLicensePlate(LICENSE_PLATE)));

        assertThrows(LicensePlateExistException.class, () -> service.createVehicle(request));
        
        verify(vehicleRepository, times(1)).findByLicensePlateAndDeletedAtIsNull(LICENSE_PLATE);
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    public void updateVehicleShouldUpdateVehicle() {
        VehicleUpdateRequest request = VehicleUpdateRequest.builder()
            .licensePlate(LICENSE_PLATE).build();

        when(vehicleRepository.findByIdAndDeletedAtIsNull(anyLong()))
            .thenReturn(Optional.of(VehicleFixture.withLicensePlate(LICENSE_PLATE)));
        when(vehicleRepository.findByLicensePlateAndDeletedAtIsNull(LICENSE_PLATE))
            .thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class)))
            .thenReturn(VehicleFixture.withLicensePlate(LICENSE_PLATE));
        when(vehicleMapper.toDomainEntity(any(Vehicle.class), any(VehicleUpdateRequest.class)))
            .thenReturn(VehicleFixture.withLicensePlate(LICENSE_PLATE));
        when(vehicleMapper.toDto(any(Vehicle.class)))
            .thenReturn(VehicleFixture.dtoWithLicensePlate(LICENSE_PLATE));


        VehicleDto result = service.updateVehicle(1L, request);

        assertEquals(LICENSE_PLATE, result.getLicensePlate());
    }

    @Test
    public void updateVehicleShouldThrowExceptionWhenVehicleNotFound() {
        VehicleUpdateRequest request = VehicleUpdateRequest.builder()
            .licensePlate(LICENSE_PLATE).build();

        when(vehicleRepository.findByIdAndDeletedAtIsNull(anyLong()))
            .thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> service.updateVehicle(1L, request));
        
        verify(vehicleRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    public void updateVehicleShouldThrowExceptionWhenLicensePlateExist() {
        VehicleUpdateRequest request = VehicleUpdateRequest.builder()
            .licensePlate(LICENSE_PLATE).build();

        when(vehicleRepository.findByIdAndDeletedAtIsNull(anyLong()))
            .thenReturn(Optional.of(VehicleFixture.withId(1L)));
        when(vehicleRepository.findByLicensePlateAndDeletedAtIsNull(LICENSE_PLATE))
            .thenReturn(Optional.of(VehicleFixture.withId(2L)));

        assertThrows(LicensePlateExistException.class, () -> service.updateVehicle(1L, request));
    
        verify(vehicleRepository, times(1)).findByLicensePlateAndDeletedAtIsNull(LICENSE_PLATE);
        verify(vehicleRepository, times(1)).findByIdAndDeletedAtIsNull(1L);
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    public void deleteVehicleShouldDeleteVehicle() {
        when(vehicleRepository.findByIdAndDeletedAtIsNull(anyLong()))
            .thenReturn(Optional.of(VehicleFixture.withId(1L)));
        when(vehicleRepository.save(any(Vehicle.class)))
            .thenReturn(VehicleFixture.withId(1L));

        Long result = service.deleteVehicle(1L);

        assertEquals(1L, result.longValue());
    }

    @Test
    public void deleteVehicleShouldThrowExceptionWhenVehicleNotFound() {
          when(vehicleRepository.findByIdAndDeletedAtIsNull(anyLong()))
            .thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> service.deleteVehicle(1L));
        
        verify(vehicleRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(vehicleMapper);
    }
}
