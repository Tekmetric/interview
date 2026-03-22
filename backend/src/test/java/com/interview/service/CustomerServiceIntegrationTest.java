package com.interview.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.interview.persistence.entity.Customer;
import com.interview.dto.request.UpdateCustomerRequest;
import com.interview.dto.response.CustomerResponse;
import com.interview.exception.CustomerNotFoundException;
import com.interview.mapper.CustomerMapper;

@SpringBootTest(
        classes = {CustomerService.class, BaseIntegrationTest.Config.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CustomerServiceIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerService customerService;

    @Test
    void findById_secondCall_hitsCacheSkipsRepository() {
        UUID id = UUID.randomUUID();
        Customer entity = new Customer();
        CustomerResponse response = CustomerResponse.builder().id(id).firstName("Jane").build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(response);

        customerService.findById(id);
        customerService.findById(id);

        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void update_putsCachedResult_subsequentFindByIdSkipsRepository() {
        UUID id = UUID.randomUUID();
        Customer entity = new Customer();
        CustomerResponse response = CustomerResponse.builder().id(id).firstName("Updated").build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerRepository.save(entity)).thenReturn(entity);
        when(customerMapper.toResponse(entity)).thenReturn(response);

        customerService.update(id, UpdateCustomerRequest.builder().firstName("Updated").build());
        customerService.findById(id);

        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void delete_evictsCache_subsequentFindByIdHitsRepository() {
        UUID id = UUID.randomUUID();
        Customer entity = new Customer();
        CustomerResponse response = CustomerResponse.builder().id(id).build();

        when(customerRepository.findById(id))
                .thenReturn(Optional.of(entity))
                .thenReturn(Optional.of(entity))
                .thenReturn(Optional.empty());
        when(customerMapper.toResponse(entity)).thenReturn(response);

        customerService.findById(id);
        customerService.delete(id);

        assertThatThrownBy(() -> customerService.findById(id))
                .isInstanceOf(CustomerNotFoundException.class);

        verify(customerRepository, times(3)).findById(id);
        verify(customerRepository).delete(entity);
    }
}
