package com.interview.api.service;

import com.interview.api.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * This interface defines operations for managing users.
 */
@Service
public interface UserService {

    /**
     * Creates a new user.
     *
     * @param userDTO The UserDTO object containing user information to be created.
     * @return The UserDTO object representing the created user.
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The UserDTO object representing the retrieved user.
     */
    UserDTO getUser(String username);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param oid The unique identifier (OID) of the user to retrieve.
     * @return The UserDTO object representing the retrieved user.
     */
    UserDTO getUser(Long oid);

    /**
     * Retrieves a paginated list of users.
     *
     * @param pageNumber    The page number of the result set to retrieve.
     * @param pageSize      The maximum number of users per page.
     * @param sortBy        The field to sort the users by.
     * @param sortDirection The direction of sorting (ASC or DESC).
     * @return A Page object containing a subset of UserDTO objects representing the users.
     */
    Page<UserDTO> getUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

    /**
     * Updates an existing user.
     *
     * @param userDTO The UserDTO object containing updated user information.
     * @return The UserDTO object representing the updated user.
     */
    UserDTO updateUser(UserDTO userDTO);

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id The unique identifier of the user to delete.
     */
    void deleteUser(Long id);
}