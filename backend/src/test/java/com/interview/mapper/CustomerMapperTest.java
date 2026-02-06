package com.interview.mapper;

import com.interview.model.dto.CustomerDTO;
import com.interview.model.dto.VehicleDTO;
import com.interview.model.entity.Customer;
import com.interview.model.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerMapperTest {

    private final CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void shouldMapDtoToEntityAndLinkVehicles() {
        VehicleDTO v1 = new VehicleDTO("1234", "Chevy", "Malibu", 2014);
        CustomerDTO dto = new CustomerDTO(
                "Jack", "McGinnis", "1234567890", Set.of(v1)
        );

        Customer customer = mapper.toEntity(dto);
        assertThat(customer.getFirstName()).isEqualTo("Jack");
        assertThat(customer.getVehicles()).hasSize(1);
        assertThat(new ArrayList<>(customer.getVehicles()).get(0).getCustomer())
                .isEqualTo(customer);
    }

    @Test
    void shouldPartialUpdateOnlyNonNullFields() {
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setFirstName("Jack");
        existingCustomer.setLastName("McGinnis");
        existingCustomer.setPhone("1234567890");

        CustomerDTO updateDto = new CustomerDTO(
                "Marisa", null, null, null
        );

        mapper.updateEntityFromDto(updateDto, existingCustomer);
        assertThat(existingCustomer.getFirstName()).isEqualTo("Marisa");
        assertThat(existingCustomer.getLastName()).isEqualTo("McGinnis");
        assertThat(existingCustomer.getPhone()).isEqualTo("1234567890");
    }

    @Test
    void shouldAddVehiclesDuringUpdate() {
        Customer existingCustomer = new Customer();
        existingCustomer.setFirstName("Jack");

        Vehicle v1 = new Vehicle();
        v1.setVin("1111");
        v1.setMake("Toyota");
        existingCustomer.addVehicle(v1);

        VehicleDTO v2 = new VehicleDTO("2222", "Chevy", "Malibu", 2012);
        CustomerDTO updateDto = new CustomerDTO(
                "Jack", "McGinnis", "1234567890", Set.of(v2)
        );

        mapper.updateEntityFromDto(updateDto, existingCustomer);
        assertThat(existingCustomer.getVehicles()).hasSize(2);
    }
}