package com.interview.service;

import com.interview.model.User;
import com.interview.repository.UserRepository;
import com.interview.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndDeletedDateIsNull(email);
        return optionalUser.map(UserPrincipal::create).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<UserDetails> loadUserById(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(UserPrincipal::create);
    }
}
