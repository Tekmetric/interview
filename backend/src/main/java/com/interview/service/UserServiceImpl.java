package com.interview.service;


import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.interview.entity.User;
import com.interview.exception.UserNotFoundException;
import com.interview.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public User addUser(User user){
        return userRepository.save(user);
    }


    public User getUser(Long aUserId) {
        Optional<User> aUser = userRepository.findById(aUserId);
        return aUser.orElseThrow(UserNotFoundException::new);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void deleteUser(Long aUserId) {
        userRepository.deleteById(aUserId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public User modifyUser(@Valid User aUser) {
        Optional<User> oldUserOptional = userRepository.findById(aUser.getUserId());
        oldUserOptional.orElseThrow(UserNotFoundException::new);
        User user =  oldUserOptional.get();
        modelMapper.map(aUser, user);
        log.debug("User email{}", user.getUserEmail());
        return userRepository.save(user);
    }
}
