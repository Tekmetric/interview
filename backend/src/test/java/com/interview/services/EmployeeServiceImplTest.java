package com.interview.services;

import com.interview.TestUtils;
import com.interview.exceptions.EmployeeNotFoundException;
import com.interview.mappers.EmployeeMapper;
import com.interview.model.*;
import com.interview.models.db.EmployeeEntity;
import com.interview.models.db.EmployeeRoleEntity;
import com.interview.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void createEmployee_shouldPersistAndReturnMappedEmployee() {
        // given
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setName("Alice");
        request.setRole(EmployeeRole.ENGINEER);
        request.setDepartment("Engineering");
        request.setEmail("alice@example.com");

        EmployeeEntity savedEntity = TestUtils.createEmployeeEntity();
        savedEntity.setName("Alice");
        savedEntity.setRole(EmployeeRoleEntity.ENGINEER);
        savedEntity.setDepartment("Engineering");
        savedEntity.setEmail("alice@example.com");

        Employee mappedEmployee = new Employee();
        mappedEmployee.setId(savedEntity.getId());
        mappedEmployee.setName(savedEntity.getName());
        mappedEmployee.setRole(EmployeeRole.ENGINEER);
        mappedEmployee.setDepartment(savedEntity.getDepartment());
        mappedEmployee.setEmail(savedEntity.getEmail());

        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(savedEntity);
        when(employeeMapper.toEmployee(savedEntity)).thenReturn(mappedEmployee);

        // when
        Employee result = employeeService.createEmployee(request);

        // then
        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals("Alice", result.getName());
        assertEquals(EmployeeRole.ENGINEER, result.getRole());
        assertEquals("Engineering", result.getDepartment());
        assertEquals("alice@example.com", result.getEmail());

        ArgumentCaptor<EmployeeEntity> entityCaptor = ArgumentCaptor.forClass(EmployeeEntity.class);
        verify(employeeRepository).save(entityCaptor.capture());
        EmployeeEntity toSave = entityCaptor.getValue();
        assertEquals("Alice", toSave.getName());
        assertEquals(EmployeeRoleEntity.ENGINEER, toSave.getRole());
        assertEquals("Engineering", toSave.getDepartment());
        assertEquals("alice@example.com", toSave.getEmail());

        verify(employeeMapper).toEmployee(savedEntity);
    }

    @Test
    void deleteEmployee_shouldDeleteWhenExists() {
        // given
        String employeeId = "emp-123";
        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        // when
        employeeService.deleteEmployee(employeeId);

        // then
        verify(employeeRepository).existsById(employeeId);
        verify(employeeRepository).deleteById(employeeId);
    }

    @Test
    void deleteEmployee_shouldThrowNotFoundWhenEmployeeDoesNotExist() {
        // given
        String employeeId = "emp-404";
        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        // when / then
        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(employeeId));

        verify(employeeRepository).existsById(employeeId);
        verify(employeeRepository, never()).deleteById(anyString());
    }

    @Test
    void getEmployeeById_shouldReturnMappedEmployeeWhenFound() {
        // given
        String employeeId = "emp-123";
        EmployeeEntity entity = TestUtils.createEmployeeEntity();
        entity.setId(employeeId);
        entity.setName("Bob");
        entity.setRole(EmployeeRoleEntity.MANAGER);

        Employee mapped = new Employee();
        mapped.setId(employeeId);
        mapped.setName("Bob");
        mapped.setRole(EmployeeRole.MANAGER);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(entity));
        when(employeeMapper.toEmployee(entity)).thenReturn(mapped);

        // when
        Employee result = employeeService.getEmployeeById(employeeId);

        // then
        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("Bob", result.getName());
        assertEquals(EmployeeRole.MANAGER, result.getRole());

        verify(employeeRepository).findById(employeeId);
        verify(employeeMapper).toEmployee(entity);
    }

    @Test
    void getEmployeeById_shouldThrowNotFoundWhenMissing() {
        // given
        String employeeId = "emp-404";
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId));

        verify(employeeRepository).findById(employeeId);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void updateEmployee_shouldUpdateFieldsAndReturnMappedEmployee() {
        // given
        String employeeId = "emp-123";

        EmployeeEntity existing = TestUtils.createEmployeeEntity();
        existing.setId(employeeId);
        existing.setName("Old Name");
        existing.setRole(EmployeeRoleEntity.ENGINEER);
        existing.setDepartment("Old Dept");
        existing.setEmail("old@example.com");

        EmployeeUpdateRequest request = new EmployeeUpdateRequest();
        request.setName("New Name");
        request.setRole(EmployeeRole.MANAGER);
        request.setDepartment("New Dept");
        request.setEmail("new@example.com");

        EmployeeEntity updatedEntity = TestUtils.createEmployeeEntity();
        updatedEntity.setId(employeeId);
        updatedEntity.setName("New Name");
        updatedEntity.setRole(EmployeeRoleEntity.MANAGER);
        updatedEntity.setDepartment("New Dept");
        updatedEntity.setEmail("new@example.com");

        Employee mapped = new Employee();
        mapped.setId(employeeId);
        mapped.setName("New Name");
        mapped.setRole(EmployeeRole.MANAGER);
        mapped.setDepartment("New Dept");
        mapped.setEmail("new@example.com");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(existing)).thenReturn(updatedEntity);
        when(employeeMapper.toEmployee(updatedEntity)).thenReturn(mapped);

        // when
        Employee result = employeeService.updateEmployee(employeeId, request);

        // then
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(EmployeeRole.MANAGER, result.getRole());
        assertEquals("New Dept", result.getDepartment());
        assertEquals("new@example.com", result.getEmail());

        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(existing);
        verify(employeeMapper).toEmployee(updatedEntity);

        // ensure entity was mutated correctly before save
        assertEquals("New Name", existing.getName());
        assertEquals(EmployeeRoleEntity.MANAGER, existing.getRole());
        assertEquals("New Dept", existing.getDepartment());
        assertEquals("new@example.com", existing.getEmail());
    }

    @Test
    void updateEmployee_shouldThrowNotFoundWhenMissing() {
        // given
        String employeeId = "emp-404";
        EmployeeUpdateRequest request = new EmployeeUpdateRequest();
        request.setName("Does Not Matter");
        request.setRole(EmployeeRole.ENGINEER);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(employeeId, request));

        verify(employeeRepository).findById(employeeId);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void listEmployees_shouldReturnPagedResultWithMappedContent() {
        // given
        int page = 0;
        int size = 2;
        PageRequest pageable = PageRequest.of(page, size);

        EmployeeEntity e1 = TestUtils.createEmployeeEntity();
        e1.setId("emp-1");
        EmployeeEntity e2 = TestUtils.createEmployeeEntity();
        e2.setId("emp-2");

        Page<EmployeeEntity> pageResult =
                new PageImpl<>(Arrays.asList(e1, e2), pageable, 5); // totalElements = 5

        Employee m1 = new Employee();
        m1.setId("emp-1");
        Employee m2 = new Employee();
        m2.setId("emp-2");

        when(employeeRepository.findAll(pageable)).thenReturn(pageResult);
        when(employeeMapper.toEmployee(e1)).thenReturn(m1);
        when(employeeMapper.toEmployee(e2)).thenReturn(m2);

        // when
        EmployeePage result = employeeService.listEmployees(page, size);

        // then
        assertNotNull(result);
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(5, result.getTotalElements());
        assertEquals(pageResult.getTotalPages(), result.getTotalPages());
        assertEquals(pageResult.isFirst(), result.getFirst());
        assertEquals(pageResult.isLast(), result.getLast());

        assertNotNull(result.getContent());
        assertEquals(2, result.getContent().size());
        assertEquals("emp-1", result.getContent().get(0).getId());
        assertEquals("emp-2", result.getContent().get(1).getId());

        verify(employeeRepository).findAll(pageable);
        verify(employeeMapper).toEmployee(e1);
        verify(employeeMapper).toEmployee(e2);
    }

    @Test
    void listEmployees_shouldHandleEmptyPage() {
        // given
        int page = 1;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);

        Page<EmployeeEntity> pageResult =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(employeeRepository.findAll(pageable)).thenReturn(pageResult);

        // when
        EmployeePage result = employeeService.listEmployees(page, size);

        // then
        assertNotNull(result);
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());

        verify(employeeRepository).findAll(pageable);
        verifyNoInteractions(employeeMapper);
    }
}
