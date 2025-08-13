package com.interview.service;

import com.interview.dtos.CustomerDto;
import com.interview.dtos.CustomerPageDto;
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

@AllArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

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
}
