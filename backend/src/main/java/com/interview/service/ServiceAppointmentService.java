package com.interview.service;

import com.interview.dto.ServiceAppointmentDTO;
import com.interview.entity.Customer;
import com.interview.entity.ServiceAppointment;
import com.interview.mapper.ServiceAppointmentMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.ServiceAppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAppointmentService.class);

    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceAppointmentMapper serviceAppointmentMapper;

    public ServiceAppointmentDTO createServiceAppointment(ServiceAppointmentDTO serviceAppointmentDTO) {
        Customer customer = customerRepository.findById(serviceAppointmentDTO.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id " + serviceAppointmentDTO.getCustomerId()));
        ServiceAppointment serviceAppointment = serviceAppointmentMapper.toEntity(serviceAppointmentDTO);
        return serviceAppointmentMapper.toDTO(serviceAppointmentRepository.save(serviceAppointment));
    }

    public Optional<ServiceAppointmentDTO> getServiceAppointmentById(Long id) {
        return serviceAppointmentRepository.findById(id).map(serviceAppointmentMapper::toDTO);
    }

    public List<ServiceAppointmentDTO> getAllServiceAppointments() {
        return serviceAppointmentRepository.findAll().stream().map(serviceAppointmentMapper::toDTO).collect(Collectors.toList());
    }

    public ServiceAppointmentDTO updateServiceAppointment(Long id, ServiceAppointmentDTO serviceAppointmentDetails) {
        ServiceAppointment serviceAppointment = serviceAppointmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ServiceAppointment not found"));
        serviceAppointment.setDescription(serviceAppointmentDetails.getDescription());
        serviceAppointment.setAppointmentDate(serviceAppointmentDetails.getAppointmentDate());

        Customer customer = customerRepository.findById(serviceAppointmentDetails.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id " + serviceAppointmentDetails.getCustomerId()));
        serviceAppointment.setCustomer(customer);
        logger.info("Updating serviceappointment" + serviceAppointment.getId());
        return serviceAppointmentMapper.toDTO(serviceAppointmentRepository.save(serviceAppointment));
    }

    public void deleteServiceAppointment(Long id) {
        try {
            logger.info("Deleting serviceappointment" + id);
            serviceAppointmentRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchElementException("ServiceAppointment not found with id " + id);
        }
    }
}
