package com.interview.service;

import com.interview.dto.UserRequest;
import com.interview.dto.UserResponse;
import com.interview.exception.BusinessRuleViolationException;
import com.interview.exception.DuplicateEntityException;
import com.interview.exception.NotFoundException;
import com.interview.mapper.UserMapper;
import com.interview.model.User;
import com.interview.repository.UserRepository;
import com.interview.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final VehicleService vehicleService;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if (user.getId().equals(securityService.getCurrentUser().getId())) {
            throw new BusinessRuleViolationException("You can't delete yourself!");
        }

        if (user.getDeletedAt() != null) {
            throw new BusinessRuleViolationException("User already deleted!");
        }

        vehicleService.deleteVehiclesByOwner(user);

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = securityService.getCurrentUser();
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(UserRequest request) {
        User currentUser = securityService.getCurrentUser();

        if (!currentUser.getUsername().equals(request.getUsername())
                && userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateEntityException("Username already exists!");
        }

        if (!currentUser.getEmailAddress().equals(request.getEmailAddress())
                && userRepository.findByEmailAddress(request.getEmailAddress()).isPresent()) {
            throw new DuplicateEntityException("Email address already exists!");
        }

        currentUser.setUsername(request.getUsername());
        currentUser.setFirstName(request.getFirstName());
        currentUser.setLastName(request.getLastName());
        currentUser.setEmailAddress(request.getEmailAddress());

        User updatedUser = userRepository.save(currentUser);
        return userMapper.toResponse(updatedUser);
    }
}
