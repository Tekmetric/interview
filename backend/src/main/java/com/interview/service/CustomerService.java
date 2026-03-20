package com.interview.service;

import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.CustomerMapper;
import com.interview.model.dto.CustomerDTO;
import com.interview.model.dto.VehicleDTO;
import com.interview.model.entity.Customer;
import com.interview.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerDTO createCustomer(CustomerDTO  dto) {
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new ResourceAlreadyExistsException("Customer with phone " + dto.getPhone() + " already exists.");
        }
        Customer customer = customerMapper.toEntity(dto);
        return customerMapper.toDTO(customerRepository.save(customer));
    }

    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customerMapper.updateEntityFromDto(dto, existing);
        if (dto.getVehicles() != null) {
            existing.getVehicles().forEach(v -> v.setCustomer(existing));
        }
        return customerMapper.toDTO(customerRepository.save(existing));
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
