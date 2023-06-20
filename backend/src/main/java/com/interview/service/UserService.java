package com.interview.service;

import com.interview.repository.UserRepository;
import com.interview.service.mapper.UserServiceMapper;
import com.interview.service.model.UserDm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserServiceMapper userServiceMapper;

    public List<UserDm> findAllUsers() {
        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .map(userServiceMapper::toDm)
                .collect(Collectors.toList());
    }
}
