package com.interview.mapper;

import com.interview.dto.CustomerDTO;
import com.interview.entity.CustomerEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CustomerMapper Unit Tests")
class CustomerMapperTest {

    @Test
    @DisplayName("Should map entity to DTO correctly with all fields")
    void shouldMapEntityToDTO_WithAllFields() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(1L);
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setEmail("john.doe@example.com");
        entity.setPhoneNumber("555-0101");

        CustomerDTO dto = CustomerMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(dto.getPhoneNumber()).isEqualTo("555-0101");
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void shouldReturnNull_WhenEntityIsNull() {
        CustomerDTO dto = CustomerMapper.toDTO(null);

        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should map DTO to entity correctly with all fields except ID")
    void shouldMapDtoToEntity_WithAllFieldsExceptId() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(999L);
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setPhoneNumber("555-0102");

        CustomerEntity entity = CustomerMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("Jane");
        assertThat(entity.getLastName()).isEqualTo("Smith");
        assertThat(entity.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(entity.getPhoneNumber()).isEqualTo("555-0102");
    }

    @Test
    @DisplayName("Should return null when DTO is null")
    void shouldReturnNull_WhenDtoIsNull() {
        CustomerEntity entity = CustomerMapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should update entity from DTO preserving ID")
    void shouldUpdateEntityFromDTO_PreservingId() {
        CustomerEntity existingEntity = new CustomerEntity();
        existingEntity.setId(1L);
        existingEntity.setFirstName("OldFirst");
        existingEntity.setLastName("OldLast");
        existingEntity.setEmail("old@example.com");
        existingEntity.setPhoneNumber("555-0000");

        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setFirstName("NewFirst");
        updateDTO.setLastName("NewLast");
        updateDTO.setEmail("new@example.com");
        updateDTO.setPhoneNumber("555-9999");

        CustomerMapper.updateEntityFromDTO(updateDTO, existingEntity);

        assertThat(existingEntity.getId()).isEqualTo(1L);
        assertThat(existingEntity.getFirstName()).isEqualTo("NewFirst");
        assertThat(existingEntity.getLastName()).isEqualTo("NewLast");
        assertThat(existingEntity.getEmail()).isEqualTo("new@example.com");
        assertThat(existingEntity.getPhoneNumber()).isEqualTo("555-9999");
    }

    @Test
    @DisplayName("Should not throw exception when updating with null DTO")
    void shouldNotThrowException_WhenUpdatingWithNullDto() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(1L);
        entity.setFirstName("John");

        assertThatCode(() -> CustomerMapper.updateEntityFromDTO(null, entity))
                .doesNotThrowAnyException();

        assertThat(entity.getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should not throw exception when updating null entity")
    void shouldNotThrowException_WhenUpdatingNullEntity() {
        CustomerDTO dto = new CustomerDTO();
        dto.setFirstName("Jane");

        assertThatCode(() -> CustomerMapper.updateEntityFromDTO(dto, null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle entity with null fields")
    void shouldHandleEntity_WithNullFields() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(1L);

        CustomerDTO dto = CustomerMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getPhoneNumber()).isNull();
    }

    @Test
    @DisplayName("Should handle DTO with null fields")
    void shouldHandleDto_WithNullFields() {
        CustomerDTO dto = new CustomerDTO();

        CustomerEntity entity = CustomerMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isNull();
        assertThat(entity.getLastName()).isNull();
        assertThat(entity.getEmail()).isNull();
        assertThat(entity.getPhoneNumber()).isNull();
    }
}
