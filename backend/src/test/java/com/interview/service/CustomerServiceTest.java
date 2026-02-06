package com.interview.service;

import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.model.dto.CustomerDTO;
import com.interview.model.dto.VehicleDTO;
import com.interview.model.entity.Customer;
import com.interview.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Jack");
        customer.setPhone("1234567890");

        customerDto = new CustomerDTO("Jack", "McGinnis", "1234567890", Set.of());
    }

    @Test
    void createCustomer_ShouldSave_WhenPhoneIsUnique() {
        when(customerRepository.existsByPhone(customerDto.getPhone())).thenReturn(false);
        when(customerMapper.toEntity(customerDto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(customerDto);

        CustomerDTO result = customerService.createCustomer(customerDto);
        assertThat(result).isNotNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_ShouldThrow_WhenPhoneExists() {
        when(customerRepository.existsByPhone(customerDto.getPhone())).thenReturn(true);
        assertThatThrownBy(() -> customerService.createCustomer(customerDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_ShouldSyncVehicles_AndSave() {
        Long id = 1L;
        VehicleDTO vDto = new VehicleDTO("VIN1234567890ABCD", "Ford", "Taurus", 2012);
        CustomerDTO updateDto = new CustomerDTO("Jack", "McGinnis", "123", Set.of(vDto));

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(updateDto);

        CustomerDTO result = customerService.updateCustomer(id, updateDto);
        verify(customerMapper).updateEntityFromDto(updateDto, customer);
        verify(customerRepository).save(customer);
        assertThat(result.getLastName()).isEqualTo("McGinnis");
    }

    @Test
    void getCustomerById_ShouldThrow_WhenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> customerService.getCustomerById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCustomer_ShouldCallRepository_WhenExists() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        customerService.deleteCustomer(1L);
        verify(customerRepository).deleteById(1L);
    }
}