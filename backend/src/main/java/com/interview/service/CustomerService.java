package com.interview.service;

import com.interview.dto.CustomerDto;
import com.interview.dto.CustomerPageDto;
import com.interview.dto.RegisterCustomerRequest;
import com.interview.entity.Address;
import com.interview.entity.Customer;
import com.interview.mappers.CustomerMapper;
import com.interview.repositories.AddressRepository;
import com.interview.repositories.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    // TODO result.content.isEmpty()
    @Cacheable(value = "customers", key = "#sort + '-' + #page + '-' + #size", unless = "#result.content.isEmpty()")
    public CustomerPageDto getCustomers(String sort, int page, int size) {
        Map<String, String> sortMapping = Map.of(
                "email", "email",
                "lastname", "lastName"
        );

        String normalizedSort = "lastName";
        if (sort != null && !sort.isBlank()) {
            normalizedSort = sortMapping.getOrDefault(sort.toLowerCase(), "lastName");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(normalizedSort));
        Page<CustomerDto> pageResult = customerRepository.findAll(pageable).map(customerMapper::toDto);

        return new CustomerPageDto(
            pageResult.getContent(),
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.getTotalPages()
        );
    }

    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<Customer> findCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer createCustomer(RegisterCustomerRequest request) {
        // Create customer entity from request
        Customer customer = customerMapper.toEntity(request);

        // Encode password
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        // Save customer first to get the generated ID
        Customer savedCustomer = customerRepository.save(customer);

        // Create and save addresses if provided
        if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
            List<Address> addresses = request.getAddresses().stream()
                    .map(addressReq -> {
                        Address address = Address.builder()
                                .street(addressReq.getStreet())
                                .city(addressReq.getCity())
                                .zip(addressReq.getZip())
                                .state(addressReq.getState())
                                .customer(savedCustomer)
                                .build();
                        return address;
                    })
                    .collect(Collectors.toList());

            List<Address> savedAddresses = addressRepository.saveAll(addresses);
            savedCustomer.setAddresses(savedAddresses);
        }

        return savedCustomer;
    }

    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Customer customer) {
        customerRepository.delete(customer);
    }

    public long countByLastName(String lastName) {
        return customerRepository.countByLastName(lastName);
    }

    @Cacheable(value = "customers-with-addresses")
    public List<CustomerDto> getAllCustomersWithAddresses() {
        List<Customer> customers = customerRepository.findAllWithAddresses();
        return customers.stream().map(customerMapper::toDto).collect(Collectors.toList());
    }
}
