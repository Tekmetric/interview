package com.interview.service;

import com.interview.dto.ServicePackageRequest;
import com.interview.dto.ServicePackageResponse;
import com.interview.dto.SubscribersResponse;
import com.interview.entity.Customer;
import com.interview.entity.ServicePackage;
import com.interview.exception.BadRequestException;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.ServicePackageNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.mapper.ServicePackageMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.ServicePackageRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServicePackageService Unit Tests")
class ServicePackageServiceTest {

    @Mock
    private ServicePackageRepository servicePackageRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServicePackageMapper servicePackageMapper;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private ServicePackageService servicePackageService;

    private ServicePackage testPackage;
    private Customer testCustomer;
    private ServicePackageRequest testRequest;
    private ServicePackageResponse testResponse;

    @BeforeEach
    void setUp() {
        // Test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setSubscribedPackages(new HashSet<>());

        // Test service package
        testPackage = new ServicePackage();
        testPackage.setId(1L);
        testPackage.setName("Basic Maintenance");
        testPackage.setDescription("Essential vehicle maintenance");
        testPackage.setMonthlyPrice(new BigDecimal("29.99"));
        testPackage.setActive(true);
        testPackage.setCreatedDate(LocalDateTime.now());
        testPackage.setUpdatedDate(LocalDateTime.now());
        testPackage.setSubscribers(new HashSet<>());

        // Test DTOs
        testRequest = new ServicePackageRequest(
            "Basic Maintenance",
            "Essential vehicle maintenance",
            new BigDecimal("29.99")
        );

        testResponse = new ServicePackageResponse(
            1L,
            "Basic Maintenance",
            "Essential vehicle maintenance",
            new BigDecimal("29.99"),
            true,
            0,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "admin",
            "admin"
        );
    }

    @Nested
    @DisplayName("Create Service Package Tests")
    class CreateServicePackageTests {

        @Test
        @DisplayName("Should create service package successfully")
        void shouldCreateServicePackageSuccessfully() {
            when(servicePackageRepository.existsByName(testRequest.name())).thenReturn(false);
            when(servicePackageMapper.toEntity(testRequest)).thenReturn(testPackage);
            when(servicePackageRepository.save(any(ServicePackage.class))).thenReturn(testPackage);
            when(servicePackageMapper.toResponseWithoutSubscribers(testPackage)).thenReturn(testResponse);

            ServicePackageResponse result = servicePackageService.createServicePackage(testRequest);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(testRequest.name());
            assertThat(result.monthlyPrice()).isEqualTo(testRequest.monthlyPrice());
            verify(servicePackageRepository).existsByName(testRequest.name());
            verify(servicePackageRepository).save(any(ServicePackage.class));
        }

        @Test
        @DisplayName("Should throw exception when package name already exists")
        void shouldThrowExceptionWhenNameExists() {
            when(servicePackageRepository.existsByName(testRequest.name())).thenReturn(true);

            assertThatThrownBy(() -> servicePackageService.createServicePackage(testRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Service package with name '" + testRequest.name() + "' already exists");

            verify(servicePackageRepository, never()).save(any(ServicePackage.class));
        }
    }

    @Nested
    @DisplayName("Get Service Package Tests")
    class GetServicePackageTests {

        @Test
        @DisplayName("Should get service package by ID successfully")
        void shouldGetServicePackageById() {
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));
            when(servicePackageMapper.toResponse(testPackage)).thenReturn(testResponse);

            ServicePackageResponse result = servicePackageService.getServicePackageById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            verify(servicePackageRepository).findByIdWithSubscribers(1L);
        }

        @Test
        @DisplayName("Should throw exception when service package not found")
        void shouldThrowExceptionWhenServicePackageNotFound() {
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicePackageService.getServicePackageById(1L))
                .isInstanceOf(ServicePackageNotFoundException.class)
                .hasMessageContaining("1");
        }

        @Test
        @DisplayName("Should get all active service packages when active=true")
        void shouldGetAllActiveServicePackages() {
            List<ServicePackage> packages = List.of(testPackage);
            when(servicePackageRepository.findAllActiveWithSubscribers()).thenReturn(packages);
            when(servicePackageMapper.toResponseList(packages)).thenReturn(List.of(testResponse));

            List<ServicePackageResponse> result = servicePackageService.getAllServicePackages(true);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().active()).isTrue();
            verify(servicePackageRepository).findAllActiveWithSubscribers();
        }

        @Test
        @DisplayName("Should get all service packages when active=null")
        void shouldGetAllServicePackagesWhenActiveNull() {
            List<ServicePackage> packages = List.of(testPackage);
            when(servicePackageRepository.findAllWithSubscribers()).thenReturn(packages);
            when(servicePackageMapper.toResponseList(packages)).thenReturn(List.of(testResponse));

            List<ServicePackageResponse> result = servicePackageService.getAllServicePackages(null);

            assertThat(result).hasSize(1);
            verify(servicePackageRepository).findAllWithSubscribers();
        }

        @Test
        @DisplayName("Should get inactive service packages when active=false")
        void shouldGetInactiveServicePackages() {
            ServicePackage inactivePackage = new ServicePackage();
            inactivePackage.setId(2L);
            inactivePackage.setName("Inactive Package");
            inactivePackage.setActive(false);

            ServicePackageResponse inactiveResponse = new ServicePackageResponse(
                2L, "Inactive Package", "Inactive", new BigDecimal("19.99"),
                false, 0, LocalDateTime.now(), LocalDateTime.now(), "admin", "admin"
            );

            List<ServicePackage> packages = List.of(testPackage, inactivePackage);
            when(servicePackageRepository.findAllWithSubscribers()).thenReturn(packages);
            when(servicePackageMapper.toResponseList(List.of(inactivePackage))).thenReturn(List.of(inactiveResponse));

            List<ServicePackageResponse> result = servicePackageService.getAllServicePackages(false);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().active()).isFalse();
            verify(servicePackageRepository).findAllWithSubscribers();
        }

        @Test
        @DisplayName("Should get service packages with pagination")
        void shouldGetServicePackagesWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<ServicePackage> packagePage = new PageImpl<>(List.of(testPackage), pageable, 1);
            when(servicePackageRepository.findAllActiveWithSubscribers(pageable)).thenReturn(packagePage);
            when(servicePackageMapper.toResponse(testPackage)).thenReturn(testResponse);

            Page<ServicePackageResponse> result = servicePackageService.getServicePackagesWithPagination(true, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Update Service Package Tests")
    class UpdateServicePackageTests {

        @Test
        @DisplayName("Should update service package successfully")
        void shouldUpdateServicePackageSuccessfully() {
            when(servicePackageRepository.findById(1L)).thenReturn(Optional.of(testPackage));
            when(servicePackageRepository.save(any(ServicePackage.class))).thenReturn(testPackage);
            when(servicePackageMapper.toResponseWithoutSubscribers(testPackage)).thenReturn(testResponse);

            ServicePackageResponse result = servicePackageService.updateServicePackage(1L, testRequest);

            assertThat(result).isNotNull();
            verify(servicePackageMapper).updateEntity(testPackage, testRequest);
            verify(servicePackageRepository).save(testPackage);
        }

        @Test
        @DisplayName("Should throw exception when service package not found for update")
        void shouldThrowExceptionWhenServicePackageNotFoundForUpdate() {
            when(servicePackageRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicePackageService.updateServicePackage(1L, testRequest))
                .isInstanceOf(ServicePackageNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when new name already exists")
        void shouldThrowExceptionWhenNewNameExists() {
            testPackage.setName("Different Name"); // Current name is different
            when(servicePackageRepository.findById(1L)).thenReturn(Optional.of(testPackage));
            when(servicePackageRepository.existsByName(testRequest.name())).thenReturn(true);

            assertThatThrownBy(() -> servicePackageService.updateServicePackage(1L, testRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Service package with name '" + testRequest.name() + "' already exists");
        }

        @Test
        @DisplayName("Should not check name existence when name unchanged")
        void shouldNotCheckNameWhenUnchanged() {
            when(servicePackageRepository.findById(1L)).thenReturn(Optional.of(testPackage));
            when(servicePackageRepository.save(any(ServicePackage.class))).thenReturn(testPackage);
            when(servicePackageMapper.toResponseWithoutSubscribers(testPackage)).thenReturn(testResponse);

            servicePackageService.updateServicePackage(1L, testRequest);

            verify(servicePackageRepository, never()).existsByName(testRequest.name());
        }
    }

    @Nested
    @DisplayName("Status Update Tests")
    class StatusUpdateTests {

        @Test
        @DisplayName("Should activate service package successfully")
        void shouldActivateServicePackage() {
            testPackage.setActive(false); // Currently inactive
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));
            when(servicePackageRepository.save(any(ServicePackage.class))).thenReturn(testPackage);
            when(servicePackageMapper.toResponse(testPackage)).thenReturn(testResponse);

            ServicePackageResponse result = servicePackageService.updateServicePackageStatus(1L, true);

            assertThat(result).isNotNull();
            verify(servicePackageRepository).save(testPackage);
        }

        @Test
        @DisplayName("Should deactivate service package successfully")
        void shouldDeactivateServicePackage() {
            testPackage.setActive(true); // Currently active
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));
            when(servicePackageRepository.save(any(ServicePackage.class))).thenReturn(testPackage);
            when(servicePackageMapper.toResponse(testPackage)).thenReturn(testResponse);

            ServicePackageResponse result = servicePackageService.updateServicePackageStatus(1L, false);

            assertThat(result).isNotNull();
            verify(servicePackageRepository).save(testPackage);
        }

        @Test
        @DisplayName("Should not update when status is already the same")
        void shouldNotUpdateWhenStatusSame() {
            testPackage.setActive(true); // Already active
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));
            when(servicePackageMapper.toResponse(testPackage)).thenReturn(testResponse);

            ServicePackageResponse result = servicePackageService.updateServicePackageStatus(1L, true);

            assertThat(result).isNotNull();
            verify(servicePackageRepository, never()).save(any(ServicePackage.class));
        }

        @Test
        @DisplayName("Should throw exception when service package not found for status update")
        void shouldThrowExceptionWhenServicePackageNotFoundForStatusUpdate() {
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicePackageService.updateServicePackageStatus(1L, true))
                .isInstanceOf(ServicePackageNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Subscription Management Tests")
    class SubscriptionManagementTests {

        @Test
        @DisplayName("Should subscribe customer to package successfully")
        void shouldSubscribeCustomerToPackage() {
            when(customerRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(testCustomer));
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));
            when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

            servicePackageService.subscribeCustomerToPackage(1L, 1L);

            verify(customerRepository).save(testCustomer);
        }

        @Test
        @DisplayName("Should throw exception when customer not found for subscription")
        void shouldThrowExceptionWhenCustomerNotFoundForSubscription() {
            when(customerRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicePackageService.subscribeCustomerToPackage(1L, 1L))
                .isInstanceOf(CustomerNotFoundException.class);

            verify(servicePackageRepository, never()).findByIdWithSubscribers(anyLong());
        }

        @Test
        @DisplayName("Should throw exception when service package not found for subscription")
        void shouldThrowExceptionWhenServicePackageNotFoundForSubscription() {
            when(customerRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(testCustomer));
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicePackageService.subscribeCustomerToPackage(1L, 1L))
                .isInstanceOf(ServicePackageNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when customer already subscribed")
        void shouldThrowExceptionWhenCustomerAlreadySubscribed() {
            testCustomer.getSubscribedPackages().add(testPackage);
            when(customerRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(testCustomer));
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));

            assertThatThrownBy(() -> servicePackageService.subscribeCustomerToPackage(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Customer is already subscribed");

            verify(customerRepository, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should unsubscribe customer from package successfully")
        void shouldUnsubscribeCustomerFromPackage() {
            testCustomer.getSubscribedPackages().add(testPackage);
            testPackage.getSubscribers().add(testCustomer);
            when(customerRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(testCustomer));
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));
            when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

            servicePackageService.unsubscribeCustomerFromPackage(1L, 1L);

            verify(customerRepository).save(testCustomer);
        }

        @Test
        @DisplayName("Should throw exception when customer not subscribed for unsubscription")
        void shouldThrowExceptionWhenCustomerNotSubscribed() {
            when(customerRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(testCustomer));
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));

            assertThatThrownBy(() -> servicePackageService.unsubscribeCustomerFromPackage(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Customer is not subscribed");

            verify(customerRepository, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should get service package subscribers successfully")
        void shouldGetServicePackageSubscribers() {
            Set<Customer> subscribers = Set.of(testCustomer);
            testPackage.setSubscribers(subscribers);
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.of(testPackage));

            SubscribersResponse result = servicePackageService.getServicePackageSubscribers(1L);

            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.subscribers()).hasSize(1);
            assertThat(result.subscribers().getFirst().email()).isEqualTo(testCustomer.getEmail());
        }

        @Test
        @DisplayName("Should throw exception when service package not found for subscribers")
        void shouldThrowExceptionWhenServicePackageNotFoundForSubscribers() {
            when(servicePackageRepository.findByIdWithSubscribers(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> servicePackageService.getServicePackageSubscribers(1L))
                .isInstanceOf(ServicePackageNotFoundException.class);
        }
    }
}