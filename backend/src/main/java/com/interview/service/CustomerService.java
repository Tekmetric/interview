package com.interview.service;

import com.interview.dto.CustomerResponse;
import com.interview.dto.PagedResponse;
import com.interview.dto.CreateCustomerRequest;
import com.interview.entity.Address;
import com.interview.entity.Customer;
import com.interview.mapper.CustomerMapper;
import com.interview.mapper.AddressMapper;
import com.interview.repository.AddressRepository;
import com.interview.repository.CustomerRepository;
import com.interview.specification.SpecificationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final PasswordEncoder passwordEncoder;

    // result.content.isEmpty(): If PagedResponse.content is empty, do not cache the result
    // the cache key must match the method's param names exactly (case-sensitive)
    // cache key example: "customers::email-0-9-beck-co"
    @Cacheable(value = "customers", key = "#sort + '-' + #page + '-' + #size + '-' + #lastName + '-' + #firstName", unless = "#result.content.isEmpty()")
    public PagedResponse<CustomerResponse> getCustomers(String sort, int page, int size, String lastName, String firstName) {
        Map<String, String> sortMapping = Map.of(
                "email", "email",
                "lastname", "lastName"
        );

        String normalizedSort = "lastName";
        if (sort != null && !sort.isBlank()) {
            normalizedSort = sortMapping.getOrDefault(sort.toLowerCase(), "lastName");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(normalizedSort));
        
        // Combine specifications using OR logic
        Specification<Customer> spec = SpecificationUtils.<Customer>fuzzySearch("lastName", lastName)
                .or(SpecificationUtils.<Customer>fuzzySearch("firstName", firstName));
        
        Page<CustomerResponse> pageResult = customerRepository.findAll(spec, pageable).map(customerMapper::toDto);

        return new PagedResponse<>(
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
    public Customer createCustomer(CreateCustomerRequest request) {
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
                        Address address = addressMapper.toEntity(addressReq);
                        address.setCustomer(savedCustomer);
                        return address;
                    })
                    .collect(Collectors.toList());

            List<Address> savedAddresses = addressRepository.saveAll(addresses);
            savedCustomer.setAddresses(savedAddresses);
        }

        return savedCustomer;
    }

    public Customer updateCustomer(Customer updatedCustomer, int customerVersionInDB, int customerVersionInRequest) {
        // Optimistic locking check
        log.info("customerVersionInDB: {}, customerVersionInRequest: {}", customerVersionInDB, customerVersionInRequest);
        if (customerVersionInRequest < customerVersionInDB) {
            String msg = "Update conflict: The customer was modified by another transaction. Please reload and try again.";
            log.warn(msg);
            throw new RuntimeException(msg);
        }
        
        // Increment version since it is updated
        updatedCustomer.setVersion(customerVersionInDB + 1);
        return updateCustomer(updatedCustomer);
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
}
