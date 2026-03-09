package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    static final Vin VIN = new Vin("1HGBH41JXMN109186");

    @Mock
    VehicleRepository vehicleRepository;

    @Spy
    final VehicleEntityMapper vehicleEntityMapper = Mappers.getMapper(VehicleEntityMapper.class);

    @InjectMocks
    VehicleService vehicleService;

    @Test
    void findAllReturnsPageOfVehicles() {
        final VehicleEntity entity = entityWith(UUID.randomUUID(), VIN);
        final PageRequest pageable = PageRequest.of(0, 20);
        when(vehicleRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));

        final Page<Vehicle> result = vehicleService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().vin()).isEqualTo(VIN);
        verify(vehicleEntityMapper).toDomain(entity);
    }

    @Test
    void findAllEmptyPage() {
        final PageRequest pageable = PageRequest.of(0, 20);
        when(vehicleRepository.findAll(pageable)).thenReturn(Page.empty());

        final Page<Vehicle> result = vehicleService.findAll(pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByIdReturnsVehicle() {
        final UUID id = UUID.randomUUID();
        final VehicleEntity entity = entityWith(id, VIN);
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(entity));

        final Vehicle result = vehicleService.findById(id);

        assertThat(result).isEqualTo(new Vehicle(id, VIN));
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
        when(vehicleRepository.save(any())).thenAnswer(invocation -> {
            final VehicleEntity e = invocation.getArgument(0);
            e.setId(savedId);
            return e;
        });

        final Vehicle vehicle = new Vehicle(null, VIN);
        final Vehicle result = vehicleService.create(vehicle);

        assertThat(result).isEqualTo(new Vehicle(savedId, VIN));
        verify(vehicleEntityMapper).toEntity(vehicle);
    }

    @Test
    void updateUpdatesAndReturnsVehicle() {
        final UUID id = UUID.randomUUID();
        final VehicleEntity existingEntity = entityWith(id, new Vin("2HGBH41JXMN109186"));
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(vehicleRepository.save(existingEntity)).thenReturn(existingEntity);

        final Vehicle vehicle = new Vehicle(id, VIN);
        final Vehicle result = vehicleService.update(id, vehicle);

        assertThat(result).isEqualTo(new Vehicle(id, VIN));
        verify(vehicleEntityMapper).updateEntity(vehicle, existingEntity);
        verify(vehicleEntityMapper).toDomain(existingEntity);
    }

    @Test
    void updateThrowsVehicleNotFound() {
        final UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(id, new Vehicle(id, VIN)))
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
