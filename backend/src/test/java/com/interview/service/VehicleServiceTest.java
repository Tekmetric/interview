package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.exception.DuplicateVinException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    private VehicleRepository repo;
    private VehicleService service;

    @BeforeEach
    void setup() {
        repo = mock(VehicleRepository.class);
        service = new VehicleService(repo);
    }

    @Test
    void create_shouldSaveVehicle() {
        VehicleRequest req = new VehicleRequest("VIN123", "Toyota", "Camry", 2020);

        Vehicle saved = new Vehicle();
        saved.setId(1L);
        saved.setVin("VIN123");
        saved.setMake("Toyota");
        saved.setModel("Camry");
        saved.setYear(2020);

        when(repo.save(any(Vehicle.class))).thenReturn(saved);

        Vehicle result = service.create(req);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getVin()).isEqualTo("VIN123");

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getVin()).isEqualTo("VIN123");
    }

    @Test
    void getAll_shouldReturnPage() {
        Vehicle v = new Vehicle();
        v.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> page = new PageImpl<>(List.of(v), pageable, 1);

        when(repo.findAll(pageable)).thenReturn(page);

        Page<Vehicle> result = service.getAll(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }


    @Test
    void getById_shouldReturnVehicle() {
        Vehicle v = new Vehicle();
        v.setId(10L);

        when(repo.findById(10L)).thenReturn(Optional.of(v));

        Vehicle result = service.getById(10L);

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_shouldModifyVehicle() {
        Vehicle existing = new Vehicle();
        existing.setId(1L);
        existing.setVin("VIN123");
        existing.setMake("Toyota");
        existing.setModel("Camry");
        existing.setYear(2020);

        VehicleRequest req = new VehicleRequest("VIN123", "Honda", "Accord", 2022);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByVinAndIdNot("VIN123", 1L)).thenReturn(false);
        when(repo.save(existing)).thenReturn(existing);

        Vehicle result = service.update(1L, req);

        assertThat(result.getMake()).isEqualTo("Honda");
        assertThat(result.getModel()).isEqualTo("Accord");
        assertThat(result.getYear()).isEqualTo(2022);
    }

    @Test
    void update_shouldThrowDuplicateVinException() {
        Vehicle existing = new Vehicle();
        existing.setId(1L);
        existing.setVin("VIN123");

        VehicleRequest req = new VehicleRequest("VIN999", "Toyota", "Camry", 2020);

        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByVinAndIdNot("VIN999", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L, req))
                .isInstanceOf(DuplicateVinException.class)
                .hasMessageContaining("VIN already exists");
    }

    @Test
    void delete_shouldRemoveVehicle() {
        Vehicle v = new Vehicle();
        v.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(v));

        service.delete(1L);

        verify(repo).delete(v);
    }
}
