package com.interview.services;

import com.interview.models.user.User;
import com.interview.models.user.dto.CreateUserRequest;
import com.interview.models.user.dto.UpdateUserRequest;
import com.interview.models.user.dto.UserResponse;
import com.interview.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create a new user
    public UserResponse createUser(CreateUserRequest createUserRequest) throws Exception {
        User user = mapDtoToEntity(createUserRequest);
        // Generate per-user key and store HMAC of password
        String key = CryptoUtil.generateRandomKeyB64();
        String hmac = CryptoUtil.hmacPasswordB64(createUserRequest.password(), key);
        user.setPasswordKey(key);
        user.setPassword(hmac);
        User savedUser = userRepository.save(user);
        return UserResponse.fromUser(savedUser);
    }

    // Get user by ID
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromUser);
    }

    // Update user
    public Optional<UserResponse> updateUser(Long id, UpdateUserRequest updateUserRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updateUserFromDto(existingUser, updateUserRequest);
                    User updatedUser = userRepository.save(existingUser);
                    return UserResponse.fromUser(updatedUser);
                });
    }

    // Delete user
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Helper method to map DTO to Entity
    private User mapDtoToEntity(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setFirstName(createUserRequest.firstName());
        user.setMiddleName(createUserRequest.middleName());
        user.setLastName(createUserRequest.lastName());
        user.setUsername(createUserRequest.username());
        // Password will be transformed in create/update methods
        user.setDateOfBirth(createUserRequest.dateOfBirth());
        user.setSsn(sanitizeDigits(createUserRequest.ssn()));
        user.setGender(createUserRequest.gender());
        user.setEmail(createUserRequest.email());
        user.setPhoneNumber(sanitizeDigits(createUserRequest.phoneNumber()));
        return user;
    }

    // Helper method to update user entity from DTO
    private void updateUserFromDto(User user, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.firstName() != null) {
            user.setFirstName(updateUserRequest.firstName());
        }
        if (updateUserRequest.middleName() != null) {
            user.setMiddleName(updateUserRequest.middleName());
        }
        if (updateUserRequest.lastName() != null) {
            user.setLastName(updateUserRequest.lastName());
        }
        if (updateUserRequest.username() != null) {
            user.setUsername(updateUserRequest.username());
        }
        if (updateUserRequest.password() != null) {
            String key = CryptoUtil.generateRandomKeyB64();
            String hmac = CryptoUtil.hmacPasswordB64(updateUserRequest.password(), key);
            user.setPasswordKey(key);
            user.setPassword(hmac);
        }
        if (updateUserRequest.dateOfBirth() != null) {
            user.setDateOfBirth(updateUserRequest.dateOfBirth());
        }
        if (updateUserRequest.ssn() != null) {
            user.setSsn(sanitizeDigits(updateUserRequest.ssn()));
        }
        if (updateUserRequest.gender() != null) {
            user.setGender(updateUserRequest.gender());
        }
        if (updateUserRequest.email() != null) {
            user.setEmail(updateUserRequest.email());
        }
        if (updateUserRequest.phoneNumber() != null) {
            user.setPhoneNumber(sanitizeDigits(updateUserRequest.phoneNumber()));
        }
    }

    private String sanitizeDigits(String input) {
        if (input == null) return null;
        return input.replaceAll("[^0-9]", "");
    }
}

