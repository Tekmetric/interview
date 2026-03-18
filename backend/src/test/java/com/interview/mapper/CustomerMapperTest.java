package com.interview.mapper;

import com.interview.dto.CustomerDTO;
import com.interview.model.Customer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerMapperTest {

    @Test
    void testToDto() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");

        // Act
        CustomerDTO dto = CustomerMapper.toDto(customer);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(customer.getId());
        assertThat(dto.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(customer.getLastName());
        assertThat(dto.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void testToDto_withNullInput_shouldReturnNull() {
        // Act
        CustomerDTO dto = CustomerMapper.toDto(null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void testToEntity() {
        // Arrange
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");

        // Act
        Customer customer = CustomerMapper.toEntity(dto);

        // Assert
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(dto.getId());
        assertThat(customer.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(dto.getLastName());
        assertThat(customer.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void testToEntity_withNullInput_shouldReturnNull() {
        // Act
        Customer customer = CustomerMapper.toEntity(null);

        // Assert
        assertThat(customer).isNull();
    }
}
