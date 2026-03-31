package com.interview.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.interview.repository.UserRepository;
import com.interview.model.User;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (!isUnique(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Someone with this name already exists in this organization.");
        }
        user.setAdministrator(shouldBeAdministrator(user));
        return userRepository.save(user);
    }

    public List<User> searchUsers(String name, String organization) {
        if (name != null && organization != null) {
            return userRepository.findByNameAndOrganization(name, organization);
        }
        if (organization != null) {
            return userRepository.findByOrganization(organization);
        }
        if (name != null) {
            return userRepository.findByName(name);
        }
        return (List<User>) userRepository.findAll();
    }

    public void deleteUser(String name, String organization) {
        List<User> users = userRepository.findByNameAndOrganization(name, organization);
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with given name and organization.");
        }
        User user = users.get(0);
        if (user.isAdministrator() && !isLastMemberInOrganization(organization)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete an administrator of an organization that still has other users.");
        }
        userRepository.delete(user);
    }

    public User getOrganizationAdministrator(String organization) {
        Optional<User> admin = userRepository.findOrganizationAdministrator(organization);
        if (!admin.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No administrator found for organization: " + organization);
        }
        return admin.get();
    }

    private boolean shouldBeAdministrator(User user) {
        return !userRepository.existsByOrganization(user.getOrganization());
    }

    private boolean isLastMemberInOrganization(String organization) {
        return userRepository.countByOrganization(organization) == 1;
    }

    private boolean isUnique(User user) {
        return !userRepository.existsByNameAndOrganization(user.getName(), user.getOrganization());
    }
}