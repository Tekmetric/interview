package com.interview.service;

import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.EmployeeDTO;
import com.interview.mapper.EntityMapper;
import com.interview.model.Employee;
import com.interview.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // Given: Common test data
    private static final String EMPLOYEE_NAME = "John Doe";
    private static final String EMPLOYEE_JOB_TITLE = "Caretaker";
    private static final String EMPLOYEE_CONTACT = "john.doe@example.com";
    private static final Long ID = 1L;
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);

    @Test
    void create_ShouldCreateEmployee_WhenValidDTOProvided() {
        // Given: A valid CreateEmployeeDTO
        CreateEmployeeDTO createEmployeeDTO = CreateEmployeeDTO.builder()
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        Employee employee = Employee.builder()
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        EmployeeDTO expectedDTO = EmployeeDTO.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        // When: Converting and saving
        when(mapper.toEntity(createEmployeeDTO)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(mapper.toDTO(employee)).thenReturn(expectedDTO);

        EmployeeDTO result = employeeService.create(createEmployeeDTO);

        // Then: The employee should be created
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(EMPLOYEE_NAME);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void findById_ShouldReturnEmployee_WhenExists() {
        // Given: An existing employee
        Employee employee = Employee.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        EmployeeDTO employeeDTO = EmployeeDTO.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        // When: Searching by ID
        when(employeeRepository.findById(ID)).thenReturn(Optional.of(employee));
        when(mapper.toDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.findById(ID);

        // Then: The employee should be found
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        // Given: A non-existent employee ID
        when(employeeRepository.findById(ID)).thenReturn(Optional.empty());

        // When/Then: Searching by ID should throw exception
        assertThatThrownBy(() -> employeeService.findById(ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Employee not found");
    }

    @Test
    void findAll_ShouldReturnAllEmployees() {
        // Given: Multiple employees exist
        Employee employee1 = Employee.builder()
            .id(1L)
            .name(EMPLOYEE_NAME)
            .build();
        Employee employee2 = Employee.builder()
            .id(2L)
            .name("Jane Doe")
            .build();
        List<Employee> employees = Arrays.asList(employee1, employee2);

        EmployeeDTO dto1 = EmployeeDTO.builder()
            .id(1L)
            .name(EMPLOYEE_NAME)
            .build();
        EmployeeDTO dto2 = EmployeeDTO.builder()
            .id(2L)
            .name("Jane Doe")
            .build();

        // When: Retrieving all employees
        Page<Employee> employeePage = new PageImpl<>(employees);
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);
        when(mapper.toDTO(eq(employee1))).thenReturn(dto1);
        when(mapper.toDTO(eq(employee2))).thenReturn(dto2);

        Page<EmployeeDTO> results = employeeService.findAll(DEFAULT_PAGEABLE);

        // Then: All employees should be returned
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent()).extracting(EmployeeDTO::getName)
            .containsExactlyInAnyOrder(EMPLOYEE_NAME, "Jane Doe");
        assertThat(results.getTotalElements()).isEqualTo(2);
    }

    @Test
    void update_ShouldUpdateEmployee_WhenExists() {
        // Given: An existing employee and update data
        Employee existingEmployee = Employee.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        EmployeeDTO updateDTO = EmployeeDTO.builder()
            .name("Updated " + EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        // When: Updating the employee
        when(employeeRepository.findById(ID)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);
        when(mapper.toDTO(existingEmployee)).thenReturn(updateDTO);

        EmployeeDTO result = employeeService.update(ID, updateDTO);

        // Then: The employee should be updated
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated " + EMPLOYEE_NAME);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void delete_ShouldDeleteEmployee_WhenExists() {
        // Given: An existing employee
        when(employeeRepository.existsById(ID)).thenReturn(true);

        // When: Deleting the employee
        employeeService.delete(ID);

        // Then: The employee should be deleted
        verify(employeeRepository).deleteById(ID);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given: A non-existent employee
        when(employeeRepository.existsById(ID)).thenReturn(false);

        // When/Then: Deleting should throw exception
        assertThatThrownBy(() -> employeeService.delete(ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Employee not found");
    }
}