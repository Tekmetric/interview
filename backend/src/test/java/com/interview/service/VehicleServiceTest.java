package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.interview.domain.Vehicle;
import com.interview.domain.Vin;
import com.interview.repository.VehicleRepository;
import com.interview.repository.entity.VehicleEntity;
import com.interview.repository.mapper.VehicleEntityMapper;
import com.interview.service.exception.VehicleNotFound;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    static final Vin VIN = new Vin("1HGBH41JXMN109186");
    static final UUID CUSTOMER_ID = UUID.randomUUID();

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    VehicleEntityMapper vehicleEntityMapper;

    @InjectMocks
    VehicleService vehicleService;

    @Test
    void findAllReturnsPageOfVehicles() {
        final VehicleEntity entity = entityWith(UUID.randomUUID(), VIN);
        final Vehicle vehicle = new Vehicle(entity.getId(), VIN, CUSTOMER_ID);
        final PageRequest pageable = PageRequest.of(0, 20);
        when(vehicleRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(vehicleEntityMapper.toDomain(entity)).thenReturn(vehicle);

        final Page<Vehicle> result = vehicleService.findAll(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().vin()).isEqualTo(VIN);
        verify(vehicleEntityMapper).toDomain(entity);
    }

    @Test
    void findAllEmptyPage() {
        final PageRequest pageable = PageRequest.of(0, 20);
        when(vehicleRepository.findAll(pageable)).thenReturn(Page.empty());

        final Page<Vehicle> result = vehicleService.findAll(null, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findAllByCustomerIdReturnsFilteredPage() {
        final VehicleEntity entity = entityWith(UUID.randomUUID(), VIN);
        final Vehicle vehicle = new Vehicle(entity.getId(), VIN, CUSTOMER_ID);
        final PageRequest pageable = PageRequest.of(0, 20);
        when(vehicleRepository.findAllByCustomerId(CUSTOMER_ID, pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(vehicleEntityMapper.toDomain(entity)).thenReturn(vehicle);

        final Page<Vehicle> result = vehicleService.findAll(CUSTOMER_ID, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().vin()).isEqualTo(VIN);
        verify(vehicleRepository).findAllByCustomerId(CUSTOMER_ID, pageable);
    }

    @Test
    void findByIdReturnsVehicle() {
        final UUID id = UUID.randomUUID();
        final VehicleEntity entity = entityWith(id, VIN);
        final Vehicle vehicle = new Vehicle(id, VIN, CUSTOMER_ID);
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(entity));
        when(vehicleEntityMapper.toDomain(entity)).thenReturn(vehicle);

        final Vehicle result = vehicleService.findById(id);

        assertThat(result).isEqualTo(vehicle);
        verify(vehicleEntityMapper).toDomain(entity);
    }

    @Test
    void findByIdThrowsVehicleNotFound() {
        final UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(id))
                .isInstanceOf(VehicleNotFound.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void createSavesAndReturnsVehicle() {
        final UUID savedId = UUID.randomUUID();
        final Vehicle input = new Vehicle(null, VIN, CUSTOMER_ID);
        final VehicleEntity entity = new VehicleEntity();
        final VehicleEntity savedEntity = entityWith(savedId, VIN);
        final Vehicle expected = new Vehicle(savedId, VIN, CUSTOMER_ID);

        when(vehicleEntityMapper.toEntity(input)).thenReturn(entity);
        when(vehicleRepository.save(entity)).thenReturn(savedEntity);
        when(vehicleEntityMapper.toDomain(savedEntity)).thenReturn(expected);

        final Vehicle result = vehicleService.create(input);

        assertThat(result).isEqualTo(expected);
        verify(vehicleEntityMapper).toEntity(input);
    }

    @Test
    void updateUpdatesAndReturnsVehicle() {
        final UUID id = UUID.randomUUID();
        final VehicleEntity existingEntity = entityWith(id, new Vin("2HGBH41JXMN109186"));
        final Vehicle input = new Vehicle(id, VIN, CUSTOMER_ID);
        final Vehicle expected = new Vehicle(id, VIN, CUSTOMER_ID);

        when(vehicleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(vehicleRepository.save(existingEntity)).thenReturn(existingEntity);
        when(vehicleEntityMapper.toDomain(existingEntity)).thenReturn(expected);

        final Vehicle result = vehicleService.update(id, input);

        assertThat(result).isEqualTo(expected);
        verify(vehicleEntityMapper).updateEntity(input, existingEntity);
        verify(vehicleEntityMapper).toDomain(existingEntity);
    }

    @Test
    void updateThrowsVehicleNotFound() {
        final UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(id, new Vehicle(id, VIN, CUSTOMER_ID)))
                .isInstanceOf(VehicleNotFound.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void deleteDeletesVehicle() {
        final UUID id = UUID.randomUUID();
        final VehicleEntity entity = entityWith(id, VIN);
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(entity));

        vehicleService.delete(id);

        verify(vehicleRepository).delete(entity);
    }

    @Test
    void deleteThrowsVehicleNotFound() {
        final UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.delete(id))
                .isInstanceOf(VehicleNotFound.class)
                .hasMessageContaining(id.toString());
    }

    private static VehicleEntity entityWith(final UUID id, final Vin vin) {
        final VehicleEntity entity = new VehicleEntity();
        entity.setId(id);
        entity.setVin(vin);
        return entity;
    }
}
