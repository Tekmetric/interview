package com.interview.tests;

import com.interview.dto.ServiceAppointmentDTO;
import com.interview.entity.Customer;
import com.interview.entity.ServiceAppointment;
import com.interview.mapper.ServiceAppointmentMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.ServiceAppointmentRepository;
import com.interview.service.ServiceAppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ServiceAppointmentServiceTest {

    @Mock
    private ServiceAppointmentRepository serviceAppointmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServiceAppointmentMapper serviceAppointmentMapper;

    @InjectMocks
    private ServiceAppointmentService serviceAppointmentService;

    public ServiceAppointmentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateServiceAppointment() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");

        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setDescription("Oil change");
        appointment.setAppointmentDate(new Date());
        appointment.setCustomer(customer);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceAppointmentRepository.save(any(ServiceAppointment.class))).thenReturn(appointment);

        ServiceAppointmentDTO newAppointment = new ServiceAppointmentDTO();
        newAppointment.setDescription("Oil change");
        newAppointment.setAppointmentDate(new Date());
        newAppointment.setCustomerId(1L);

        when(serviceAppointmentMapper.toDTO(any(ServiceAppointment.class))).thenReturn(newAppointment);
        when(serviceAppointmentMapper.toEntity(any(ServiceAppointmentDTO.class))).thenReturn(appointment);


        ServiceAppointmentDTO createdAppointment = serviceAppointmentService.createServiceAppointment(newAppointment);

        assertNotNull(createdAppointment);
        assertEquals("Oil change", createdAppointment.getDescription());
        assertEquals(customer.getId(), createdAppointment.getCustomerId());
    }

    @Test
    void testCreateServiceAppointment_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        ServiceAppointmentDTO newAppointment = new ServiceAppointmentDTO();
        newAppointment.setDescription("Oil change");
        newAppointment.setAppointmentDate(new Date());
        newAppointment.setCustomerId(1L);

        when(serviceAppointmentMapper.toDTO(any(ServiceAppointment.class))).thenReturn(newAppointment);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            serviceAppointmentService.createServiceAppointment(newAppointment);
        });

        assertEquals("Customer not found with id 1", exception.getMessage());
    }

    // Add more tests for other methods (update, delete, etc.)
}
