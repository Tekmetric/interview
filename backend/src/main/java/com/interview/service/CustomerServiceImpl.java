package com.interview.service;

import com.interview.model.Customer;
import com.interview.model.CustomerDTO;
import com.interview.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO employeeDTO) {
        Customer employee = convertToEntity(employeeDTO);
        Customer savedEmployee = customerRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO employeeDTO) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        customer.setEmail(employeeDTO.getEmail());
        Customer updatedEmployee = customerRepository.save(customer);
        return convertToDTO(updatedEmployee);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);

    }

    private CustomerDTO convertToDTO(Customer customer) {
        return new CustomerDTO(customer.getId(), customer.getEmail());
    }

    private Customer convertToEntity(CustomerDTO employeeDTO) {
        Customer customer = new Customer();
        customer.setEmail(employeeDTO.getEmail());
        return customer;
    }
}
