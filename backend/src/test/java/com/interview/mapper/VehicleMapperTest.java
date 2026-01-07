package com.interview.mapper;

import com.interview.dto.VehicleDTO;
import com.interview.entity.CustomerEntity;
import com.interview.entity.VehicleEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("VehicleMapper Unit Tests")
class VehicleMapperTest {

    @Test
    @DisplayName("Should map entity to DTO correctly with all fields")
    void shouldMapEntityToDTO_WithAllFields() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);

        VehicleEntity entity = new VehicleEntity();
        entity.setId(1L);
        entity.setMake("Toyota");
        entity.setModel("Camry");
        entity.setModelYear(2023);
        entity.setVin("1HGBH41JXMN109186");
        entity.setCustomer(customer);

        VehicleDTO dto = VehicleMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getMake()).isEqualTo("Toyota");
        assertThat(dto.getModel()).isEqualTo("Camry");
        assertThat(dto.getModelYear()).isEqualTo(2023);
        assertThat(dto.getVin()).isEqualTo("1HGBH41JXMN109186");
        assertThat(dto.getCustomerId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void shouldReturnNull_WhenEntityIsNull() {
        VehicleDTO dto = VehicleMapper.toDTO(null);

        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should handle entity with null customer")
    void shouldHandleEntity_WithNullCustomer() {
        VehicleEntity entity = new VehicleEntity();
        entity.setId(1L);
        entity.setMake("Toyota");
        entity.setModel("Camry");
        entity.setModelYear(2023);
        entity.setVin("1HGBH41JXMN109186");
        entity.setCustomer(null);

        VehicleDTO dto = VehicleMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getMake()).isEqualTo("Toyota");
        assertThat(dto.getCustomerId()).isNull();
    }

    @Test
    @DisplayName("Should map DTO to entity correctly with all fields except ID")
    void shouldMapDtoToEntity_WithAllFieldsExceptId() {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(999L);
        dto.setMake("Honda");
        dto.setModel("Accord");
        dto.setModelYear(2024);
        dto.setVin("1HGCV1F30LA123456");
        dto.setCustomerId(2L);

        VehicleEntity entity = VehicleMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getMake()).isEqualTo("Honda");
        assertThat(entity.getModel()).isEqualTo("Accord");
        assertThat(entity.getModelYear()).isEqualTo(2024);
        assertThat(entity.getVin()).isEqualTo("1HGCV1F30LA123456");
        assertThat(entity.getCustomer()).isNull();
    }

    @Test
    @DisplayName("Should return null when DTO is null")
    void shouldReturnNull_WhenDtoIsNull() {
        VehicleEntity entity = VehicleMapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should update entity from DTO preserving ID and customer")
    void shouldUpdateEntityFromDTO_PreservingIdAndCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);

        VehicleEntity existingEntity = new VehicleEntity();
        existingEntity.setId(1L);
        existingEntity.setMake("OldMake");
        existingEntity.setModel("OldModel");
        existingEntity.setModelYear(2020);
        existingEntity.setVin("OLDVIN1234567890A");
        existingEntity.setCustomer(customer);

        VehicleDTO updateDTO = new VehicleDTO();
        updateDTO.setMake("NewMake");
        updateDTO.setModel("NewModel");
        updateDTO.setModelYear(2024);
        updateDTO.setVin("NEWVIN1234567890A");
        updateDTO.setCustomerId(2L);

        VehicleMapper.updateEntityFromDTO(updateDTO, existingEntity);

        assertThat(existingEntity.getId()).isEqualTo(1L);
        assertThat(existingEntity.getMake()).isEqualTo("NewMake");
        assertThat(existingEntity.getModel()).isEqualTo("NewModel");
        assertThat(existingEntity.getModelYear()).isEqualTo(2024);
        assertThat(existingEntity.getVin()).isEqualTo("NEWVIN1234567890A");
        assertThat(existingEntity.getCustomer()).isEqualTo(customer);
    }

    @Test
    @DisplayName("Should not throw exception when updating with null DTO")
    void shouldNotThrowException_WhenUpdatingWithNullDto() {
        VehicleEntity entity = new VehicleEntity();
        entity.setId(1L);
        entity.setMake("Toyota");

        assertThatCode(() -> VehicleMapper.updateEntityFromDTO(null, entity))
                .doesNotThrowAnyException();

        assertThat(entity.getMake()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Should not throw exception when updating null entity")
    void shouldNotThrowException_WhenUpdatingNullEntity() {
        VehicleDTO dto = new VehicleDTO();
        dto.setMake("Honda");

        assertThatCode(() -> VehicleMapper.updateEntityFromDTO(dto, null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle entity with null fields")
    void shouldHandleEntity_WithNullFields() {
        VehicleEntity entity = new VehicleEntity();
        entity.setId(1L);

        VehicleDTO dto = VehicleMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getMake()).isNull();
        assertThat(dto.getModel()).isNull();
        assertThat(dto.getModelYear()).isNull();
        assertThat(dto.getVin()).isNull();
        assertThat(dto.getCustomerId()).isNull();
    }

    @Test
    @DisplayName("Should handle DTO with null fields")
    void shouldHandleDto_WithNullFields() {
        VehicleDTO dto = new VehicleDTO();

        VehicleEntity entity = VehicleMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getMake()).isNull();
        assertThat(entity.getModel()).isNull();
        assertThat(entity.getModelYear()).isNull();
        assertThat(entity.getVin()).isNull();
        assertThat(entity.getCustomer()).isNull();
    }

    @Test
    @DisplayName("Should correctly extract customer ID from nested customer entity")
    void shouldExtractCustomerId_FromNestedCustomerEntity() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(42L);
        customer.setEmail("test@example.com");

        VehicleEntity entity = new VehicleEntity();
        entity.setId(1L);
        entity.setMake("Tesla");
        entity.setModel("Model S");
        entity.setModelYear(2024);
        entity.setVin("5YJ3E1EA1KF123456");
        entity.setCustomer(customer);

        VehicleDTO dto = VehicleMapper.toDTO(entity);

        assertThat(dto.getCustomerId()).isEqualTo(42L);
    }
}
