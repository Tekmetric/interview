package com.interview.service;

import com.interview.dto.VehicleDTO;
import com.interview.entity.CustomerEntity;
import com.interview.entity.VehicleEntity;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleDTO validVehicleDTO;
    private VehicleEntity vehicleEntity;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        validVehicleDTO = new VehicleDTO();
        validVehicleDTO.setMake("Toyota");
        validVehicleDTO.setModel("Camry");
        validVehicleDTO.setModelYear(2023);
        validVehicleDTO.setVin("1HGBH41JXMN109186");
        validVehicleDTO.setCustomerId(1L);

        customerEntity = new CustomerEntity();
        customerEntity.setId(1L);
        customerEntity.setEmail("customer@example.com");

        vehicleEntity = new VehicleEntity();
        vehicleEntity.setId(1L);
        vehicleEntity.setMake("Toyota");
        vehicleEntity.setModel("Camry");
        vehicleEntity.setModelYear(2023);
        vehicleEntity.setVin("1HGBH41JXMN109186");
        vehicleEntity.setCustomer(customerEntity);
    }

    @Test
    @DisplayName("Should create vehicle successfully when VIN is unique and customer exists")
    void shouldCreateVehicle_WhenVinIsUniqueAndCustomerExists() {
        when(vehicleRepository.existsByVin(anyString())).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerEntity));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);

        VehicleDTO result = vehicleService.createVehicle(validVehicleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getVin()).isEqualTo("1HGBH41JXMN109186");
        assertThat(result.getMake()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Camry");
        assertThat(result.getCustomerId()).isEqualTo(1L);
        
        verify(vehicleRepository).existsByVin("1HGBH41JXMN109186");
        verify(customerRepository).findById(1L);
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when VIN already exists")
    void shouldThrowDuplicateResourceException_WhenVinAlreadyExists() {
        when(vehicleRepository.existsByVin(anyString())).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.createVehicle(validVehicleDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("VIN")
                .hasMessageContaining("1HGBH41JXMN109186");

        verify(vehicleRepository).existsByVin("1HGBH41JXMN109186");
        verify(customerRepository, never()).findById(anyLong());
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer does not exist")
    void shouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        when(vehicleRepository.existsByVin(anyString())).thenReturn(false);
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        validVehicleDTO.setCustomerId(999L);

        assertThatThrownBy(() -> vehicleService.createVehicle(validVehicleDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer")
                .hasMessageContaining("999");

        verify(vehicleRepository).existsByVin(anyString());
        verify(customerRepository).findById(999L);
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should get vehicle by ID successfully when vehicle exists")
    void shouldGetVehicleById_WhenVehicleExists() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));

        VehicleDTO result = vehicleService.getVehicleById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getVin()).isEqualTo("1HGBH41JXMN109186");
        
        verify(vehicleRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle ID not found")
    void shouldThrowResourceNotFoundException_WhenVehicleIdNotFound() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining("999");

        verify(vehicleRepository).findById(999L);
    }

    @Test
    @DisplayName("Should update vehicle successfully when VIN is unchanged")
    void shouldUpdateVehicle_WhenVinIsUnchanged() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);

        validVehicleDTO.setMake("Honda");
        VehicleDTO result = vehicleService.updateVehicle(1L, validVehicleDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        
        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository).save(any(VehicleEntity.class));
        verify(vehicleRepository, never()).existsByVin(anyString());
    }

    @Test
    @DisplayName("Should update vehicle successfully when new VIN is unique")
    void shouldUpdateVehicle_WhenNewVinIsUnique() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.existsByVin("NEWVIN1234567890A")).thenReturn(false);
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);

        validVehicleDTO.setVin("NEWVIN1234567890A");
        VehicleDTO result = vehicleService.updateVehicle(1L, validVehicleDTO);

        assertThat(result).isNotNull();
        
        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository).existsByVin("NEWVIN1234567890A");
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updating with existing VIN")
    void shouldThrowDuplicateResourceException_WhenUpdatingWithExistingVin() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.existsByVin("EXISTINGVIN123456")).thenReturn(true);

        validVehicleDTO.setVin("EXISTINGVIN123456");

        assertThatThrownBy(() -> vehicleService.updateVehicle(1L, validVehicleDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("VIN")
                .hasMessageContaining("EXISTINGVIN123456");

        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository).existsByVin("EXISTINGVIN123456");
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should update vehicle customer when customer ID changes")
    void shouldUpdateVehicleCustomer_WhenCustomerIdChanges() {
        CustomerEntity newCustomer = new CustomerEntity();
        newCustomer.setId(2L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(newCustomer));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);

        validVehicleDTO.setCustomerId(2L);
        VehicleDTO result = vehicleService.updateVehicle(1L, validVehicleDTO);

        assertThat(result).isNotNull();
        
        verify(vehicleRepository).findById(1L);
        verify(customerRepository).findById(2L);
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating with non-existent customer")
    void shouldThrowResourceNotFoundException_WhenUpdatingWithNonExistentCustomer() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        validVehicleDTO.setCustomerId(999L);

        assertThatThrownBy(() -> vehicleService.updateVehicle(1L, validVehicleDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer")
                .hasMessageContaining("999");

        verify(vehicleRepository).findById(1L);
        verify(customerRepository).findById(999L);
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent vehicle")
    void shouldThrowResourceNotFoundException_WhenUpdatingNonExistentVehicle() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.updateVehicle(999L, validVehicleDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining("999");

        verify(vehicleRepository).findById(999L);
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    @DisplayName("Should delete vehicle successfully when vehicle exists")
    void shouldDeleteVehicle_WhenVehicleExists() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        assertThatCode(() -> vehicleService.deleteVehicle(1L))
                .doesNotThrowAnyException();

        verify(vehicleRepository).existsById(1L);
        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent vehicle")
    void shouldThrowResourceNotFoundException_WhenDeletingNonExistentVehicle() {
        when(vehicleRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.deleteVehicle(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle")
                .hasMessageContaining("999");

        verify(vehicleRepository).existsById(999L);
        verify(vehicleRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should get all vehicles without filters")
    void shouldGetAllVehicles_WithoutFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<VehicleEntity> vehiclePage = new PageImpl<>(List.of(vehicleEntity));
        when(vehicleRepository.findAll(pageable)).thenReturn(vehiclePage);

        Page<VehicleDTO> result = vehicleService.getAllVehicles(null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getVin()).isEqualTo("1HGBH41JXMN109186");
        
        verify(vehicleRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should filter vehicles by VIN")
    void shouldFilterVehicles_ByVin() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<VehicleEntity> vehiclePage = new PageImpl<>(List.of(vehicleEntity));
        when(vehicleRepository.findByVinContainingIgnoreCase("1HGC", pageable)).thenReturn(vehiclePage);

        Page<VehicleDTO> result = vehicleService.getAllVehicles("1HGC", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(vehicleRepository).findByVinContainingIgnoreCase("1HGC", pageable);
        verify(vehicleRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should get vehicles by customer ID")
    void shouldGetVehiclesByCustomerId() {
        when(vehicleRepository.findByCustomerId(1L)).thenReturn(List.of(vehicleEntity));

        List<VehicleDTO> result = vehicleService.getVehiclesByCustomerId(1L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        
        verify(vehicleRepository).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should return empty list when customer has no vehicles")
    void shouldReturnEmptyList_WhenCustomerHasNoVehicles() {
        when(vehicleRepository.findByCustomerId(999L)).thenReturn(List.of());

        List<VehicleDTO> result = vehicleService.getVehiclesByCustomerId(999L);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(vehicleRepository).findByCustomerId(999L);
    }
}
