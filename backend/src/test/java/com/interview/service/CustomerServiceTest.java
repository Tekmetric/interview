package com.interview.service;

import com.interview.dto.CustomerRequest;
import com.interview.dto.CustomerResponse;
import com.interview.entity.Customer;
import com.interview.entity.CustomerProfile;
import com.interview.enums.ContactMethod;
import com.interview.exception.BadRequestException;
import com.interview.exception.CustomerAlreadyExistsException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.OptimisticLockingException;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerProfile testProfile;
    private CustomerRequest testRequest;
    private CustomerResponse testResponse;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setVersion(0L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("+1-555-0101");
        testCustomer.setCreatedDate(LocalDateTime.now());
        testCustomer.setUpdatedDate(LocalDateTime.now());

        testProfile = new CustomerProfile();
        testProfile.setId(1L);
        testProfile.setCustomer(testCustomer);
        testProfile.setAddress("123 Main St");
        testProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testProfile.setPreferredContactMethod(ContactMethod.EMAIL);
        testCustomer.setCustomerProfile(testProfile);

        // Test DTOs
        testRequest = new CustomerRequest(
            "John",
            0L,
            "Doe",
            "john.doe@example.com",
            "+1-555-0101",
            "123 Main St",
            LocalDate.of(1990, 1, 1),
            ContactMethod.EMAIL
        );

        testResponse = new CustomerResponse(
            1L,
            0L,
            "John",
            "Doe",
            "john.doe@example.com",
            "+1-555-0101",
            "123 Main St",
            LocalDate.of(1990, 1, 1),
            ContactMethod.EMAIL,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "admin",
            "admin"
        );
    }

    @Nested
    @DisplayName("Create Customer Tests")
    class CreateCustomerTests {

        @Test
        @DisplayName("Should create customer successfully with profile")
        void shouldCreateCustomerWithProfile() {
            when(customerRepository.existsByEmail(testRequest.email())).thenReturn(false);
            when(customerMapper.toEntity(testRequest)).thenReturn(testCustomer);
            when(customerMapper.toProfileEntity(testRequest)).thenReturn(testProfile);
            when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
            when(customerMapper.toResponse(testCustomer)).thenReturn(testResponse);

            CustomerResponse result = customerService.createCustomer(testRequest);

            assertThat(result).isNotNull();
            assertThat(result.email()).isEqualTo(testRequest.email());
            verify(customerRepository).existsByEmail(testRequest.email());
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should create customer without profile when no profile data provided")
        void shouldCreateCustomerWithoutProfile() {
            CustomerRequest requestWithoutProfile = new CustomerRequest(
                "John", 0L, "Doe", "john.doe@example.com", "+1-555-0101",
                null, null, null
            );
            when(customerRepository.existsByEmail(requestWithoutProfile.email())).thenReturn(false);
            when(customerMapper.toEntity(requestWithoutProfile)).thenReturn(testCustomer);
            when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
            when(customerMapper.toResponse(testCustomer)).thenReturn(testResponse);

            CustomerResponse result = customerService.createCustomer(requestWithoutProfile);

            assertThat(result).isNotNull();
            verify(customerMapper, never()).toProfileEntity(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(customerRepository.existsByEmail(testRequest.email())).thenReturn(true);

            assertThatThrownBy(() -> customerService.createCustomer(testRequest))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining(testRequest.email());

            verify(customerRepository, never()).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Get Customer Tests")
    class GetCustomerTests {

        @Test
        @DisplayName("Should get customer by ID successfully")
        void shouldGetCustomerById() {
            when(customerRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testCustomer));
            when(customerMapper.toResponse(testCustomer)).thenReturn(testResponse);

            CustomerResponse result = customerService.getCustomerById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            verify(customerRepository).findByIdWithProfile(1L);
        }

        @Test
        @DisplayName("Should throw exception when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            when(customerRepository.findByIdWithProfile(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.getCustomerById(1L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("1");
        }

        @Test
        @DisplayName("Should get all customers successfully")
        void shouldGetAllCustomers() {
            List<Customer> customers = List.of(testCustomer);
            when(customerRepository.findAllWithProfiles()).thenReturn(customers);
            when(customerMapper.toResponseList(customers)).thenReturn(List.of(testResponse));

            List<CustomerResponse> result = customerService.getAllCustomers();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should get customers with pagination successfully")
        void shouldGetCustomersWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> customerPage = new PageImpl<>(List.of(testCustomer), pageable, 1);
            when(customerRepository.findAllWithProfiles(pageable)).thenReturn(customerPage);
            when(customerMapper.toResponse(testCustomer)).thenReturn(testResponse);

            Page<CustomerResponse> result = customerService.getCustomersWithPagination(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Update Customer Tests")
    class UpdateCustomerTests {

        @Test
        @DisplayName("Should update customer successfully")
        void shouldUpdateCustomerSuccessfully() {
            when(customerRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testCustomer));
            when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

            customerService.updateCustomer(1L, testRequest);

            verify(customerMapper).updateEntity(testCustomer, testRequest);
            verify(customerRepository).save(testCustomer);
        }

        @Test
        @DisplayName("Should throw exception when version is null")
        void shouldThrowExceptionWhenVersionIsNull() {
            CustomerRequest requestWithoutVersion = new CustomerRequest(
                "John", null, "Doe", "john.doe@example.com", "+1-555-0101",
                "123 Main St", LocalDate.of(1990, 1, 1), ContactMethod.EMAIL
            );

            assertThatThrownBy(() -> customerService.updateCustomer(1L, requestWithoutVersion))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Version is required");

            verify(customerRepository, never()).findByIdWithProfile(anyLong());
        }

        @Test
        @DisplayName("Should throw exception when customer not found for update")
        void shouldThrowExceptionWhenCustomerNotFoundForUpdate() {
            when(customerRepository.findByIdWithProfile(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.updateCustomer(1L, testRequest))
                .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw optimistic locking exception when versions don't match")
        void shouldThrowOptimisticLockingException() {
            testCustomer.setVersion(1L); // Different version
            when(customerRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testCustomer));

            assertThatThrownBy(() -> customerService.updateCustomer(1L, testRequest))
                .isInstanceOf(OptimisticLockingException.class)
                .hasMessageContaining("modified by another user");
        }

        @Test
        @DisplayName("Should throw exception when new email already exists")
        void shouldThrowExceptionWhenNewEmailExists() {
            CustomerRequest requestWithDifferentEmail = new CustomerRequest(
                "John", 0L, "Doe", "different@example.com", "+1-555-0101",
                "123 Main St", LocalDate.of(1990, 1, 1), ContactMethod.EMAIL
            );
            when(customerRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testCustomer));
            when(customerRepository.existsByEmail("different@example.com")).thenReturn(true);

            assertThatThrownBy(() -> customerService.updateCustomer(1L, requestWithDifferentEmail))
                .isInstanceOf(CustomerAlreadyExistsException.class);
        }

        @Test
        @DisplayName("Should create profile when customer has no profile but request has profile data")
        void shouldCreateProfileWhenCustomerHasNoProfile() {
            testCustomer.setCustomerProfile(null); // No existing profile
            when(customerRepository.findByIdWithProfile(1L)).thenReturn(Optional.of(testCustomer));
            when(customerMapper.toProfileEntity(testRequest)).thenReturn(testProfile);
            when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

            customerService.updateCustomer(1L, testRequest);

            verify(customerMapper).toProfileEntity(testRequest);
            verify(customerRepository).save(testCustomer);
        }
    }

    @Nested
    @DisplayName("Delete Customer Tests")
    class DeleteCustomerTests {

        @Test
        @DisplayName("Should delete customer successfully")
        void shouldDeleteCustomerSuccessfully() {
            when(customerRepository.deleteByCustomerId(1L)).thenReturn(1);

            customerService.deleteCustomer(1L);

            verify(customerRepository).deleteByCustomerId(1L);
        }

        @Test
        @DisplayName("Should throw exception when customer not found for deletion")
        void shouldThrowExceptionWhenCustomerNotFoundForDeletion() {
            when(customerRepository.deleteByCustomerId(1L)).thenReturn(0);

            assertThatThrownBy(() -> customerService.deleteCustomer(1L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("1");
        }
    }
}