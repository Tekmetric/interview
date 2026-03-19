package com.interview.service;

import com.interview.dto.CreateEmployeeRequest;
import com.interview.dto.EmployeeResponse;
import com.interview.dto.UpdateEmployeeRequest;
import com.interview.entity.Employee;
import com.interview.entity.EmploymentStatus;
import com.interview.entity.Gender;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private CreateEmployeeRequest validCreateRequest;
    private Employee savedEmployee;

    @BeforeEach
    void setUp() {
        validCreateRequest = new CreateEmployeeRequest();
        validCreateRequest.setFirstname("Jane");
        validCreateRequest.setLastname("Doe");
        validCreateRequest.setHiredDate(LocalDate.of(2024, 1, 15));
        validCreateRequest.setGender(Gender.FEMALE);
        validCreateRequest.setEmploymentStatus(EmploymentStatus.ACTIVE);
        validCreateRequest.setYearlySalary(new BigDecimal("75000.00"));

        savedEmployee = new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setVersion(0L);
        savedEmployee.setFirstname("Jane");
        savedEmployee.setLastname("Doe");
        savedEmployee.setHiredDate(LocalDate.of(2024, 1, 15));
        savedEmployee.setGender(Gender.FEMALE);
        savedEmployee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        savedEmployee.setYearlySalary(new BigDecimal("75000.00"));
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("saves entity and returns response with id and version")
        void create_returnsResponse() {
            when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
                Employee e = inv.getArgument(0);
                e.setId(1L);
                e.setVersion(0L);
                return e;
            });

            EmployeeResponse result = employeeService.create(validCreateRequest);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getVersion()).isEqualTo(0L);
            assertThat(result.getFirstname()).isEqualTo("Jane");
            assertThat(result.getLastname()).isEqualTo("Doe");
            assertThat(result.getGender()).isEqualTo(Gender.FEMALE);
            assertThat(result.getEmploymentStatus()).isEqualTo(EmploymentStatus.ACTIVE);
            assertThat(result.getYearlySalary()).isEqualByComparingTo("75000.00");

            ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
            verify(employeeRepository).save(captor.capture());
            assertThat(captor.getValue().getFirstname()).isEqualTo("Jane");
            assertThat(captor.getValue().getTermDate()).isNull();
        }

        @Test
        @DisplayName("maps termDate when provided")
        void create_withTermDate_mapsToEntity() {
            validCreateRequest.setTermDate(LocalDate.of(2024, 6, 30));
            when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
                Employee e = inv.getArgument(0);
                e.setId(2L);
                e.setVersion(0L);
                return e;
            });

            EmployeeResponse result = employeeService.create(validCreateRequest);

            assertThat(result.getTermDate()).isEqualTo(LocalDate.of(2024, 6, 30));
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("returns paginated responses")
        void findAll_returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(employeeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(savedEmployee), pageable, 1));

            Page<EmployeeResponse> result = employeeService.findAll(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).getFirstname()).isEqualTo("Jane");
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(employeeRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("returns response when employee exists")
        void findById_whenExists_returnsResponse() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(savedEmployee));

            EmployeeResponse result = employeeService.findById(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstname()).isEqualTo("Jane");
            verify(employeeRepository).findById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void findById_whenNotExists_throws() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.findById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Employee")
                    .hasMessageContaining("999");
            verify(employeeRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("updates and returns response when employee exists")
        void update_whenExists_returnsUpdated() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(savedEmployee));
            when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateEmployeeRequest request = new UpdateEmployeeRequest();
            request.setFirstname("Janet");
            request.setYearlySalary(new BigDecimal("80000.00"));

            EmployeeResponse result = employeeService.update(1L, request);

            assertThat(result.getFirstname()).isEqualTo("Janet");
            assertThat(result.getYearlySalary()).isEqualByComparingTo("80000.00");
            assertThat(result.getLastname()).isEqualTo("Doe"); // unchanged

            ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
            verify(employeeRepository).save(captor.capture());
            assertThat(captor.getValue().getFirstname()).isEqualTo("Janet");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void update_whenNotExists_throws() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            UpdateEmployeeRequest request = new UpdateEmployeeRequest();
            request.setFirstname("X");

            assertThatThrownBy(() -> employeeService.update(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
            verify(employeeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {
        @Test
        @DisplayName("deletes when employee exists")
        void deleteById_whenExists_deletes() {
            when(employeeRepository.existsById(1L)).thenReturn(true);
            doNothing().when(employeeRepository).deleteById(1L);

            employeeService.deleteById(1L);

            verify(employeeRepository).existsById(1L);
            verify(employeeRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void deleteById_whenNotExists_throws() {
            when(employeeRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> employeeService.deleteById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
            verify(employeeRepository).existsById(999L);
            verify(employeeRepository, never()).deleteById(any());
        }
    }
}
