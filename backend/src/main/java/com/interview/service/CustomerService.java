package com.interview.service;

import com.interview.dto.CustomerCreateDto;
import com.interview.dto.CustomerDto;
import com.interview.entity.Customer;
import com.interview.exception.NotFoundException;
import com.interview.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    private CustomerDto toDto(Customer c) {
        return new CustomerDto(c.getId(), c.getName(), c.getEmail());
    }

    public List<CustomerDto> findAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<CustomerDto> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDto);
    }

    public CustomerDto findById(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    @Transactional
    public CustomerDto create(CustomerCreateDto dto) {
        // optionally check uniqueness
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Customer c = new Customer(dto.getName(), dto.getEmail());
        Customer saved = repo.save(c);
        return toDto(saved);
    }

    @Transactional
    public CustomerDto update(Long id, CustomerCreateDto dto) {
        Customer existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        Customer saved = repo.save(existing);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Customer not found: " + id);
        }
        repo.deleteById(id);
    }
}