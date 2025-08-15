package com.interview.service;

import com.interview.entity.Customer;
import com.interview.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@AllArgsConstructor
@Service
// This service is used for user login authentication by Spring Security
public class UserService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        return new User(
                customer.getEmail(),
                customer.getPassword(),
                Collections.emptyList()
        );
    }
}