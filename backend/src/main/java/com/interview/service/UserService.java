package com.interview.service;

import com.interview.model.User;
import com.interview.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service class for handling User-related business logic.
 * Provides methods to manage User entities.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    public List<User> findAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user
     * @return the user
     * @throws EntityNotFoundException if user not found
     */
    public User findUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    /**
     * Saves a user.
     *
     * @param user the user to save
     * @return the saved user
     */
    public User saveUser(User user) {
        logger.info("Saving user: {}", user);
        return userRepository.save(user);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @throws EntityNotFoundException if user not found
     */
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}