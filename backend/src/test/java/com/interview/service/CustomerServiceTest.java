// CustomerServiceTest.java (junit + mockito)
package com.interview.service;

import com.interview.dto.CustomerCreateDto;
import com.interview.entity.Customer;
import com.interview.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class CustomerServiceTest {

    @Test
    void createShouldSave() {
        var repo = Mockito.mock(CustomerRepository.class);
        Mockito.when(repo.existsByEmail("x@x.com")).thenReturn(false);
        Mockito.when(repo.save(any(Customer.class))).thenAnswer(i -> {
            Customer c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        var svc = new CustomerService(repo);
        var dto = new CustomerCreateDto();
        dto.setName("X");
        dto.setEmail("x@x.com");

        var result = svc.create(dto);
        assertEquals(1L, result.getId());
        assertEquals("X", result.getName());
    }
}
