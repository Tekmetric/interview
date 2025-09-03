package com.interview.services;

import com.interview.domain.Vehicle;
import com.interview.dtos.VehiclePatchDTO;
import com.interview.dtos.VehicleRequestDTO;
import com.interview.dtos.VehicleResponseDTO;
import com.interview.exceptions.ResourceAlreadyExistsException;
import com.interview.exceptions.ResourceNotFoundException;
import com.interview.mappers.VehicleMapper;
import com.interview.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle buildVehicle() {
        return Vehicle.builder()
                .id(1L)
                .vin("1HGBH41JXMN109186")
                .make("Honda")
                .model("Civic")
                .ownerName("Logan")
                .build();
    }

    private VehicleRequestDTO buildVehicleRequestDTO() {
        return VehicleRequestDTO.builder()
                .vin("1HGBH41JXMN109186")
                .make("Honda")
                .model("Civic")
                .ownerName("Logan")
                .build();
    }

    private VehicleResponseDTO buildVehicleResponseDTO() {
        return VehicleResponseDTO.builder()
                .id(1L)
                .vin("1HGBH41JXMN109186")
                .make("Honda")
                .model("Civic")
                .ownerName("Logan")
                .build();
    }

    private VehiclePatchDTO buildVehiclePatchDTO() {
        return VehiclePatchDTO.builder()
                .vin("2T1BURHE0JC014702")
                .make("Toyota")
                .build();
    }

    @Test
    void findById_WhenVehicleExists_ShouldReturnVehicleResponseDTO() {
        Long vehicleId = 1L;
        Vehicle vehicle = buildVehicle();
        VehicleResponseDTO expectedResponse = buildVehicleResponseDTO();

        when(repository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(expectedResponse);

        VehicleResponseDTO result = vehicleService.findById(vehicleId);

        assertThat(result).isEqualTo(expectedResponse);
        verify(repository).findById(vehicleId);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void findById_WhenVehicleNotFound_ShouldThrowResourceNotFoundException() {
        Long vehicleId = 999L;
        when(repository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(vehicleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining("999");

        verify(repository).findById(vehicleId);
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    void findAll_ShouldReturnMappedPageOfVehicleResponseDTOs() {
        Pageable pageable = PageRequest.of(0, 10);

        Vehicle vehicle1 = buildVehicle();
        Vehicle vehicle2 = Vehicle.builder()
                .id(2L)
                .vin("2T1BURHE0JC014702")
                .make("Toyota")
                .model("Corolla")
                .ownerName("Mike")
                .build();

        VehicleResponseDTO dto1 = buildVehicleResponseDTO();
        VehicleResponseDTO dto2 = VehicleResponseDTO.builder()
                .id(2L)
                .vin("2T1BURHE0JC014702")
                .make("Toyota")
                .model("Corolla")
                .ownerName("Mike")
                .build();

        when(repository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(vehicle1, vehicle2), pageable, 2));
        when(vehicleMapper.toDto(vehicle1)).thenReturn(dto1);
        when(vehicleMapper.toDto(vehicle2)).thenReturn(dto2);

        Page<VehicleResponseDTO> result = vehicleService.findAll(pageable);

        assertThat(result.getContent())
                .isNotNull()
                .hasSize(2)
                .containsExactly(dto1, dto2);

        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(repository).findAll(pageable);
        verify(vehicleMapper).toDto(vehicle1);
        verify(vehicleMapper).toDto(vehicle2);
        verifyNoMoreInteractions(repository, vehicleMapper);
    }


    @Test
    void create_WhenVinIsUnique_ShouldCreateVehicle() {
        VehicleRequestDTO requestDTO = buildVehicleRequestDTO();
        Vehicle vehicle = buildVehicle();
        VehicleResponseDTO expectedResponse = buildVehicleResponseDTO();

        when(repository.existsByVinAndIdNot(requestDTO.vin(), null)).thenReturn(false);
        when(vehicleMapper.toEntity(requestDTO)).thenReturn(vehicle);
        when(repository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(expectedResponse);

        VehicleResponseDTO result = vehicleService.create(requestDTO);

        assertThat(result).isEqualTo(expectedResponse);
        verify(repository).existsByVinAndIdNot(requestDTO.vin(), null);
        verify(vehicleMapper).toEntity(requestDTO);
        verify(repository).save(vehicle);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void create_WhenVinAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        VehicleRequestDTO requestDTO = buildVehicleRequestDTO();
        when(repository.existsByVinAndIdNot(requestDTO.vin(), null)).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.create(requestDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Vehicle with VIN already exists")
                .hasMessageContaining(requestDTO.vin());

        verify(repository).existsByVinAndIdNot(requestDTO.vin(), null);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    void update_WhenVehicleExistsAndVinIsUnique_ShouldUpdateVehicle() {
        Long vehicleId = 1L;
        VehicleRequestDTO requestDTO = VehicleRequestDTO.builder()
                .vin("UPDATED_VIN_123456")
                .make("Updated Make")
                .model("Updated Model")
                .ownerName("Updated Owner")
                .build();

        Vehicle existingVehicle = buildVehicle();
        VehicleResponseDTO expectedResponse = VehicleResponseDTO.builder()
                .id(1L)
                .vin("UPDATED_VIN_123456")
                .make("Updated Make")
                .model("Updated Model")
                .ownerName("Updated Owner")
                .build();

        when(repository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(repository.existsByVinAndIdNot(requestDTO.vin(), vehicleId)).thenReturn(false);
        when(repository.save(existingVehicle)).thenReturn(existingVehicle);
        when(vehicleMapper.toDto(existingVehicle)).thenReturn(expectedResponse);

        VehicleResponseDTO result = vehicleService.update(vehicleId, requestDTO);

        assertThat(result).isEqualTo(expectedResponse);
        verify(repository).findById(vehicleId);
        verify(repository).existsByVinAndIdNot(requestDTO.vin(), vehicleId);
        verify(vehicleMapper).updateEntity(requestDTO, existingVehicle);
        verify(repository).save(existingVehicle);
        verify(vehicleMapper).toDto(existingVehicle);
    }

    @Test
    void update_WhenVehicleNotFound_ShouldThrowResourceNotFoundException() {
        Long vehicleId = 999L;
        VehicleRequestDTO requestDTO = buildVehicleRequestDTO();
        when(repository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(vehicleId, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining("999");

        verify(repository).findById(vehicleId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    void update_WhenVinAlreadyExistsForDifferentVehicle_ShouldThrowResourceAlreadyExistsException() {
        Long vehicleId = 1L;
        VehicleRequestDTO requestDTO = buildVehicleRequestDTO();
        Vehicle existingVehicle = buildVehicle();

        when(repository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(repository.existsByVinAndIdNot(requestDTO.vin(), vehicleId)).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.update(vehicleId, requestDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Vehicle with VIN already exists")
                .hasMessageContaining(requestDTO.vin());

        verify(repository).findById(vehicleId);
        verify(repository).existsByVinAndIdNot(requestDTO.vin(), vehicleId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(vehicleMapper);
    }

    @Test
    void patch_WhenVehicleExistsAndVinIsUnique_ShouldPatchVehicle() {
        Long vehicleId = 1L;
        VehiclePatchDTO patchDTO = buildVehiclePatchDTO();
        Vehicle existingVehicle = buildVehicle();
        VehicleResponseDTO expectedResponse = VehicleResponseDTO.builder()
                .id(1L)
                .vin("2T1BURHE0JC014702")
                .make("Toyota")
                .model("Civic") // Only make and vin updated in patch
                .ownerName("Logan")
                .build();

        when(repository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(repository.existsByVinAndIdNot(patchDTO.vin(), vehicleId)).thenReturn(false);
        when(repository.save(existingVehicle)).thenReturn(existingVehicle);
        when(vehicleMapper.toDto(existingVehicle)).thenReturn(expectedResponse);

        VehicleResponseDTO result = vehicleService.patch(vehicleId, patchDTO);

        assertThat(result).isEqualTo(expectedResponse);
        verify(repository).findById(vehicleId);
        verify(repository).existsByVinAndIdNot(patchDTO.vin(), vehicleId);
        verify(vehicleMapper).patchEntity(patchDTO, existingVehicle);
        verify(repository).save(existingVehicle);
        verify(vehicleMapper).toDto(existingVehicle);
    }

    @Test
    void patch_WhenVinIsNull_ShouldNotCheckVinUniqueness() {
        Long vehicleId = 1L;
        VehiclePatchDTO patchDTO = VehiclePatchDTO.builder()
                .make("Toyota")
                .vin(null)
                .build();

        Vehicle existingVehicle = buildVehicle();
        VehicleResponseDTO expectedResponse = buildVehicleResponseDTO();

        when(repository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(repository.save(existingVehicle)).thenReturn(existingVehicle);
        when(vehicleMapper.toDto(existingVehicle)).thenReturn(expectedResponse);

        VehicleResponseDTO result = vehicleService.patch(vehicleId, patchDTO);

        assertThat(result).isEqualTo(expectedResponse);
        verify(repository).findById(vehicleId);
        verify(repository, never()).existsByVinAndIdNot(any(), any());
        verify(vehicleMapper).patchEntity(patchDTO, existingVehicle);
        verify(repository).save(existingVehicle);
        verify(vehicleMapper).toDto(existingVehicle);
    }

    @Test
    void patch_WhenVinAlreadyExistsForDifferentVehicle_ShouldThrowResourceAlreadyExistsException() {
        Long vehicleId = 1L;
        VehiclePatchDTO patchDTO = buildVehiclePatchDTO();
        Vehicle existingVehicle = buildVehicle();

        when(repository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(repository.existsByVinAndIdNot(patchDTO.vin(), vehicleId)).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.patch(vehicleId, patchDTO))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Vehicle with VIN already exists")
                .hasMessageContaining(patchDTO.vin());

        verify(repository).findById(vehicleId);
        verify(repository).existsByVinAndIdNot(patchDTO.vin(), vehicleId);
        verifyNoMoreInteractions(repository);
        verify(vehicleMapper, never()).patchEntity(any(), any());
    }

    @Test
    void delete_WhenVehicleExists_ShouldDeleteVehicle() {
        Long vehicleId = 1L;
        when(repository.existsById(vehicleId)).thenReturn(true);

        vehicleService.delete(vehicleId);

        verify(repository).existsById(vehicleId);
        verify(repository).deleteById(vehicleId);
    }

    @Test
    void delete_WhenVehicleNotFound_ShouldThrowResourceNotFoundException() {
        Long vehicleId = 999L;
        when(repository.existsById(vehicleId)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.delete(vehicleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining("999");

        verify(repository).existsById(vehicleId);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void findByVin_WhenVehicleExists_ShouldReturnVehicleResponseDTO() {
        String vin = "1HGBH41JXMN109186";
        Vehicle vehicle = buildVehicle();
        VehicleResponseDTO expectedResponse = buildVehicleResponseDTO();

        when(repository.findByVin(vin)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(expectedResponse);

        VehicleResponseDTO result = vehicleService.findByVin(vin);

        assertThat(result).isEqualTo(expectedResponse);
        verify(repository).findByVin(vin);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void findByVin_WhenVehicleNotFound_ShouldThrowResourceNotFoundException() {
        String vin = "NONEXISTENT123456";
        when(repository.findByVin(vin)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findByVin(vin))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining(vin);

        verify(repository).findByVin(vin);
        verifyNoInteractions(vehicleMapper);
    }
}