package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.interview.persistence.entity.Customer;
import com.interview.persistence.enums.EmploymentStatus;
import com.interview.dto.request.CreateCustomerRequest;
import com.interview.dto.request.embedded.AddressRequest;
import com.interview.dto.request.embedded.EmploymentDetailsRequest;
import com.interview.dto.response.CustomerResponse;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.DuplicateResourceException;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void create_savesAndReturnsResponse() {
        CreateCustomerRequest request = buildCreateRequest();
        Customer entity = new Customer();
        CustomerResponse response = buildResponse();

        when(customerMapper.toEntity(request)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(entity);
        when(customerMapper.toResponse(entity)).thenReturn(response);

        CustomerResponse result = customerService.create(request);

        assertThat(result).isEqualTo(response);
        verify(customerRepository).save(entity);
    }

    @Test
    void create_duplicateEmail_throwsDuplicateResourceException() {
        CreateCustomerRequest request = buildCreateRequest();
        when(customerMapper.toEntity(request)).thenReturn(new Customer());
        when(customerRepository.save(any())).thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> customerService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void findById_existingId_returnsResponse() {
        UUID id = UUID.randomUUID();
        Customer entity = new Customer();
        CustomerResponse response = buildResponse();

        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));
        when(customerMapper.toResponse(entity)).thenReturn(response);

        assertThat(customerService.findById(id)).isEqualTo(response);
    }

    @Test
    void findById_missingId_throwsCustomerNotFoundException() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(id))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void delete_existingId_callsRepositoryDelete() {
        UUID id = UUID.randomUUID();
        Customer entity = new Customer();
        when(customerRepository.findById(id)).thenReturn(Optional.of(entity));

        customerService.delete(id);

        verify(customerRepository).delete(entity);
    }

    @Test
    void delete_missingId_throwsCustomerNotFoundException() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.delete(id))
                .isInstanceOf(CustomerNotFoundException.class);
        verify(customerRepository, never()).delete(any());
    }

    private CreateCustomerRequest buildCreateRequest() {
        return CreateCustomerRequest.builder()
                .firstName("Jane").lastName("Doe").email("jane@example.com")
                .phone("+15555550100").dateOfBirth(LocalDate.of(1985, 3, 15)).ssn("123-45-6789")
                .address(AddressRequest.builder().street("1 St").city("Austin").state("TX").zipCode("78701").build())
                .employmentDetails(EmploymentDetailsRequest.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerName("Acme").annualIncome(BigDecimal.valueOf(95000)).build())
                .build();
    }

    private CustomerResponse buildResponse() {
        return CustomerResponse.builder().id(UUID.randomUUID()).firstName("Jane").lastName("Doe")
                .email("jane@example.com").build();
    }
}
