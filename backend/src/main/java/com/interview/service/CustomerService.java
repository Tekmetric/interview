package com.interview.service;

import com.interview.dto.CustomerDto;
import com.interview.dto.CustomerPageDto;
import com.interview.entity.Customer;
import com.interview.mappers.CustomerMapper;
import com.interview.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

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

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
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
