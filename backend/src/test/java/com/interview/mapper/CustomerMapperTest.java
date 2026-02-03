package com.interview.mapper;

import com.interview.model.dto.CustomerDTO;
import com.interview.model.entity.Customer;
import com.interview.model.entity.Vehicle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

public class CustomerMapperTest {

    private final CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    public void shouldMapCustomerToDto() {
        Vehicle v1 = new Vehicle();
        v1.setId(101L);
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("Jack")
                .lastName("McGinnis")
                .phone("1234567890")
                .vehicles(List.of(v1))
                .build();

        CustomerDTO dto = mapper.toDto(customer);
        Assertions.assertEquals(customer.getId(), dto.getId());
        Assertions.assertEquals("Jack", dto.getFirstName());
        Assertions.assertEquals(1, dto.getVehicleIds().size());
        Assertions.assertEquals(v1.getId(), dto.getVehicleIds().get(0));
    }

    @Test
    public void shouldMapDtoToCustomer() {
        CustomerDTO dto = CustomerDTO.builder()
                .id(1L)
                .firstName("Jack")
                .lastName("McGinnis")
                .phone("1234567890")
                .vehicleIds(List.of(1L))
                .build();

        Customer customer = mapper.toEntity(dto);
        Assertions.assertEquals(customer.getId(), dto.getId());
        Assertions.assertEquals("Jack", dto.getFirstName());
        Assertions.assertEquals(1, dto.getVehicleIds().size());
    }
}